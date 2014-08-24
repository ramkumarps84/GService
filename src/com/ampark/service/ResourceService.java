package com.ampark.service;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Component;

import com.ampark.db.GridFsTemplateHelper;
import com.ampark.domain.Resource;
import com.ampark.domain.StringLookup;
import com.ampark.repository.ResourceRepository;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Component
public class ResourceService {

	static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
	
	@Autowired
	private  ResourceRepository resourceRepository;
	
	@Autowired
	private GridFsTemplateHelper gfsTemplateHelper;
	
	
	private String buildQuery(Map<String,String> allParams){
    	String query = null;
    	if (allParams.get("searchType") == null){
    		query= "{}";
    	}else 	if(allParams.get("searchType").equals("text")){
    		query = "{$text:{$search:\""+allParams.get("searchString")+"\"}}";
    	}else if(allParams.get("searchType").equals("field")){
    		query = "{\""+allParams.get("searchKey")+"\":{$regex:\""+allParams.get("searchString")+"\",$options:'i'}}";
    	}else if(allParams.get("searchType").equals("array")){
    		query = "{\""+allParams.get("searchKey")+"\":{$in:"+allParams.get("searchString")+"}}";
    	}
    	logger.debug(query);
    	return query;
    	
    }


	public List<Resource> findAll(Map<String, String> allParams) {
		return resourceRepository.findAll(allParams.get("resourceType"),buildQuery(allParams),Integer.parseInt(allParams.get("page").toString()),Integer.parseInt(allParams.get("limit").toString()));
	}


	public Resource findOne(Map<String, String> allParams) {
		return resourceRepository.findOne(allParams.get("resourceType"),buildQuery(allParams));
	}


	public List<Resource> findAllDocuments(Map<String, String> allParams) {
		return resourceRepository.findAllDocuments(allParams.get("resourceType"),buildQuery(allParams),Integer.parseInt(allParams.get("page").toString()),Integer.parseInt(allParams.get("limit").toString()));
	}


	public Resource findOneDocument(Map<String, String> allParams) {
		return resourceRepository.findOneDocument(allParams.get("resourceType"),buildQuery(allParams));
	}


	public List<StringLookup> getDistinctValueForAttr(Map<String, String> allParams) {
		return resourceRepository.getDistinctValueForAttr(allParams.get("resourceType"),Boolean.parseBoolean(allParams.get("isMediaType").toString()),allParams.get("attributeName"),buildQuery(allParams));
	}


	public DBObject findAndUpdateResourceByID(Map<String, String> allParams) {
		return resourceRepository.findAndUpdateResourceByID(allParams.get("resourceType"),allParams.get("resourceId"),JSON.parse(allParams.get("attributes").toString()));
	}


	public Resource getResourceForCredentials(String username, String mobile) {
        Resource user = resourceRepository.findOne("OTC_User", "{\"username\":\""+username+"\",\"mobile\":\""+mobile+"\"}");
        return user;
	}


	public List<StringLookup> getAttrValue(Map<String, String> allParams) {
		return resourceRepository.getAttrValue(allParams.get("resourceType"),Boolean.parseBoolean(allParams.get("isMediaType").toString()),allParams.get("attributeName"),buildQuery(allParams));
	}


	public void storeFile(String bucket,InputStream file, String fileName, String mimeType,
			DBObject metadata) {
		
		try {
			((GridFsOperations)gfsTemplateHelper.getGridFsTemplate(bucket)).store(file, fileName,mimeType,metadata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public DBObject addToFavorites(Map<String, String> allParams) {
		return resourceRepository.addToFavorites(allParams.get("resourceType"),allParams.get("username"),JSON.parse(allParams.get("attributes").toString()));
	}


	public Resource createResource(Resource resource) {
		resourceRepository.createResource(resource);
		return resource;
	}
}
