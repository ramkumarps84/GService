package com.ampark.testunit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ampark.service.ResourceService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:com/ampark/testunit/applicationContext.xml"})
public class TestGridFS {

	@Autowired
	private  ResourceService resourceServiceImpl;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@Test
	//@Ignore
	public void testCreate() {
		DBObject metaData = new BasicDBObject();
		/*metaData.put("MANUFACTURER", "ACTO");
		metaData.put("ITEM", "ACELOFAN");
		metaData.put("CATEGORY", "FEATURED");
		metaData.put("QUANTITY", "10");
		metaData.put("DEAL_START_DATE", "10-07-2014");
		metaData.put("DEAL_END_DATE", "20-09-2014");*/
		
		/*metaData.put("MANUFACTURER", "BLUBELL");
		metaData.put("ITEM", "BELFIX");
		metaData.put("CATEGORY", "H");
		metaData.put("QUANTITY", "10");
		metaData.put("DEAL_START_DATE", "20-06-2014");
		metaData.put("DEAL_END_DATE", "10-07-2014");*/
		 		
		metaData.put("MANUFACTURER", "HETERO HC");
		metaData.put("ITEM", "ALFAPSIN");
		metaData.put("CATEGORY", "FEATURED");
		metaData.put("QUANTITY", "10");
		metaData.put("DEAL_START_DATE", "06-06-2014");
		metaData.put("DEAL_END_DATE", "30-08-2014");
								

		
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("C:\\Users\\Public\\Pictures\\Desert_Small.jpg");
			resourceServiceImpl.storeFile("OTC_Deals",inputStream, "Desert_Small2.jpg", "image/jpg", metaData);
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
		
}
