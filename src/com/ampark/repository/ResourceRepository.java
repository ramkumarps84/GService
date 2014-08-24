package com.ampark.repository;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;

import com.ampark.db.GridFsTemplateHelper;
import com.ampark.domain.Resource;
import com.ampark.domain.StringLookup;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.util.JSON;

@Repository
public class ResourceRepository {

	static final Logger logger = LoggerFactory.getLogger(ResourceRepository.class);

    @Autowired
    MongoTemplate mongoTemplate;
    
    @Autowired
    private GridFsTemplateHelper gfsTemplateHelper;

    public List<Resource> findAll(String collectionName,String query,Integer page, Integer limit){
    	
    	if(!mongoTemplate.collectionExists(collectionName))
    		return null;
    	
    	List<Resource> response = new ArrayList<Resource>();
    	Resource res = null;
    	String key = null;
    	
    	try{
    	List<DBObject> qryResults =  mongoTemplate.find(new BasicQuery(query).with(new PageRequest(page-1, limit)), DBObject.class, collectionName);
    	logger.debug(qryResults.size()+ " Resources found");
    	for(DBObject item: qryResults){
    		res = new Resource();
    		populateResourceObject(item, res, key);
    		response.add(res);
    	}
    	
    	}catch(Exception e){
    		logger.error("Exception while running findAll",e.getMessage());
    		e.printStackTrace();
    	}finally{
    		logger.debug("Cleaning up findAll");
    	}
    	return response;
    }
    
    public Resource findOne(String collectionName,String jsonQuery){
    	
    	if(!mongoTemplate.collectionExists(collectionName))
    		return null;
    	
    	Resource response = null;
    	String key = null;
    	
    	try{
    	DBObject qryResult =  mongoTemplate.findOne(new BasicQuery(jsonQuery), DBObject.class, collectionName);
    		response = new Resource();
    		if(qryResult!=null){
    		populateResourceObject(qryResult,response,key);
    		}else{
    			return null;
    		}
    		
    	}catch(Exception e){
    		logger.error("Exception while running findAll",e.getMessage());
    		e.printStackTrace();
    	}finally{
    		logger.debug("Cleaning up findAll");
    	}
    	return response;
    }
    

	public List<Resource> findAllDocuments(String bucket,String query,Integer page, Integer limit){

		
		List<Resource> result = new ArrayList<Resource>();
    	try {
    			DBCursor gridResults = mongoTemplate.getCollection(bucket+".files").find(new BasicQuery(query).getQueryObject());
    			gridResults.limit(limit);
    			gridResults.skip(page-1);
				logger.debug(gridResults.size()+ " Documents found");
				while (gridResults.hasNext()){
					Resource res = null;
					String key = null;
					DBObject itrObj = null;
					res = new Resource();
					itrObj = gridResults.next();
					GridFS gfs = new GridFS(mongoTemplate.getDb(), bucket);
					GridFSDBFile image = gfs.findOne(new ObjectId(itrObj.get("_id").toString()));
					populateResourceObject(image.getMetaData(), res, key);
					res.setResourceId(image.getId().toString());
					byte[] fileArray = IOUtils.toByteArray(image.getInputStream());
					byte[] bytes64 = Base64.encodeBase64(fileArray);
					String content = new String(bytes64);
					res.addResourceAttribute("mediaContent",content );
					result.add(res);
				}
				gridResults.close();
		} catch (Exception e) {
			logger.error("Exception while running findAllDocuments",e.getMessage());
			e.printStackTrace();
		}
    	return result;
    }
	
	public Resource findOneDocument(String bucket, String query) {

		GridFSDBFile gridResult = null;
		Resource res = null;
		try {
			gridResult = ((GridFsOperations) gfsTemplateHelper.getGridFsTemplate(bucket)).findOne(new BasicQuery(query));

			String key = null;
			res = new Resource();
			populateResourceObject(gridResult.getMetaData(), res, key);
			byte[] fileArray = IOUtils.toByteArray(gridResult.getInputStream());
			byte[] bytes64 = Base64.encodeBase64(fileArray);
			String content = new String(bytes64);
			res.addResourceAttribute("mediaContent", content);
		} catch (Exception e) {
			logger.error("Exception while running findAllDocuments",
					e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	public DBObject findAndUpdateResourceByID(String resourceType,String id, Object object){
		DBObject qry = new BasicDBObject();
		qry.put("_id", new ObjectId(id));
		DBObject res = mongoTemplate.getCollection(resourceType).findAndModify(qry, null, null, false,new BasicDBObject("$set",object),true,false);
		return res; 
	}
	

	public List<StringLookup> getDistinctValueForAttr(String documentType, Boolean isMediaType, String attributeName,String query) {
		if(!isMediaType && !mongoTemplate.collectionExists(documentType))
			return null;
		if(isMediaType)
		documentType = documentType+".files";
		List cur =mongoTemplate.getCollection(documentType).distinct(attributeName,new BasicQuery(query).getQueryObject());
		List<StringLookup> rs = new ArrayList<StringLookup>();
		for (Object itemVal: cur){
			rs.add(new StringLookup(itemVal.toString()));
		}
		return rs;
	}
	
	public void createResource(Resource resource) {
		if(!mongoTemplate.collectionExists(resource.getResourceType()))
			mongoTemplate.createCollection(resource.getResourceType());
		DBObject dbObject = null;
		if(resource.getAllDirectAttributes().containsKey("isMediaType") && resource.getAllDirectAttributes().get("isMediaType").equals("true"))
		{
			storeFile(resource.getAllDirectAttributes().get("bucket").toString(),(InputStream)resource.getResourceAttribute("mediaContent" ,InputStream.class), resource.getResourceAttribute("fileName" ,String.class).toString(), resource.getResourceAttribute("mimeType" ,String.class).toString(), new BasicDBObject(resource.getAllDirectAttributes()));
			logger.info("Successfully Loaded file");
		}else
		{
			dbObject = (DBObject) JSON.parse(resource.toString());
			mongoTemplate.getCollection(resource.getResourceType()).insert(dbObject);
			/*Iterator<String> detailIterator = resource.getAllDirectAttributes().keySet().iterator();
			Query query =null;
			DBObject obj =null;
			DBObject pObj = null;
			String nextAttr=null;
			while(detailIterator.hasNext()){
				nextAttr = detailIterator.next();
				query = new Query();
				obj = new BasicDBObject();
				obj.put("name", nextAttr);
				
				if(!template.collectionExists(resource.getResourceType()+"_ATTR"))
					template.createCollection(resource.getResourceType()+"_ATTR");
				pObj = template.getCollection(resource.getResourceType()+"_ATTR").findOne(query.addCriteria(Criteria.where("name").is(nextAttr)).getQueryObject());
				if(pObj==null)
				template.getCollection(resource.getResourceType()+"_ATTR").insert(obj);
					
				System.out.println(query.getQueryObject());
			}*/
			logger.info("Successfully Submitted"+ dbObject.toString());
		}
		
		
	}
	
	public void storeFile(String bucket,InputStream file, String fileName, String mimeType,
			DBObject metadata) {
		
		try {
			metadata.removeField("isMediaType");
			metadata.removeField("fileName");
			metadata.removeField("bucket");
			((GridFsOperations)gfsTemplateHelper.getGridFsTemplate(bucket)).store(file, fileName,mimeType,metadata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception while storing file "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Utility method
	 */
    private void populateResourceObject(DBObject qryResult, Resource res,String key) {
    	Iterator attrIter = qryResult.toMap().keySet().iterator();
		while(attrIter.hasNext()){
			key = attrIter.next().toString();
			 if(key.equals("resourceType"))
					res.setResourceType( qryResult.get("resourceType").toString());
				 else if (key.equals("_id"))
					 res.setResourceId(((ObjectId)qryResult.get(key)).toString());
				else if (key.contains("_id"))
						res.addResourceAttribute(key,qryResult.get(key));
					else if(qryResult.get(key)!=null){
						res.addResourceAttribute(key, qryResult.get(key));
					}
		}
}

	public List<StringLookup> getAttrValue(String documentType, Boolean isMediaType, String attributeName,String query) {
		if(!isMediaType && !mongoTemplate.collectionExists(documentType))
			return null;
		if(isMediaType)
		documentType = documentType+".files";
		
		DBObject cur =mongoTemplate.getCollection(documentType).findOne(new BasicQuery(query).getQueryObject(), new BasicDBObject(attributeName, 1));
		Object attrVal = cur.get(attributeName);
		List<StringLookup> rs = new ArrayList<StringLookup>();
		
		if(attrVal != null){
			rs.add(new StringLookup(attrVal.toString()));
		}
		return rs;
	}

		public DBObject addToFavorites(String resourceType,String username, Object object){
			DBObject qry = new BasicDBObject();
			qry.put("username", username);
			DBObject res = mongoTemplate.getCollection(resourceType).findAndModify(qry, null, null, false,new BasicDBObject("$set",object),true,false);
			return res; 
		}
	
	
}
