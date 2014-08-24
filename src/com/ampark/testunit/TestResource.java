package com.ampark.testunit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ampark.controller.ResourceController;
import com.ampark.domain.Resource;
import com.ampark.domain.StringLookup;
import com.ampark.repository.ResourceRepository;
import com.ampark.service.ResourceService;
import com.mongodb.DBObject;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:com/ampark/testunit/applicationContext.xml"})
public class TestResource {

	@Autowired
	private  ResourceService resourceService;
	
	@Autowired
	private  ResourceController resourceController;
	
	@Autowired
	private ResourceRepository resourceRepository;
	
	@Test
	//@Ignore
	public void testFindAllDocuments(){
		
		HashMap map = new HashMap();
		map.put("resourceType","OTC_Deals");
		map.put("isMediaType","true");
		//map.put("attributes", "{'data':{'Favorite':'true'}}");
		//map.put("resourceId", "53adc72d00c749fa0d54f932");
		map.put("page","1");
		map.put("limit","10");
		//map.put("searchType","field");
		//map.put("searchKey","metadata.MANUFACTURER");
		map.put("searchString","ACT");
		//System.out.println(resourceController.updateReource(map));
		//resourceController.findAllDocuments(map);
		//map.put("page","2");
		//map.put("limit","1");
		//resourceController.findAllDocuments(map);
		resourceController.findAllDocuments(map);
	}
	
	@Test
	@Ignore
	public void testGetAttrValue(){
		List<StringLookup> results = resourceRepository.getDistinctValueForAttr("OTC_Location",false,"City", "{}");
		System.out.println(results.size());
	}
	
	@Test
	@Ignore
	public void testUpdate(){
		HashMap<String,Object> attr = new HashMap<String, Object>();
		attr.put("State", "Tamilnadu");
		DBObject results = resourceRepository.findAndUpdateResourceByID("OTC_Location", "53adc72d00c749fa0d54f932", attr);
		Assert.assertNotNull(results);
	}
	
	@Test	
	@Ignore
	public void testCreateResource(){
		Resource newResource=new Resource();
		newResource.setResourceType("OTC_User");
		
		newResource.addResourceAttribute("firstName", "Karthik");
		newResource.addResourceAttribute("lastName", "Sankaran");
		newResource.addResourceAttribute("favoriteManufacturers", new ArrayList<String>());
		MessageDigest md=null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md.update("password@098".getBytes());
        //Get the hash's bytes 
        byte[] bytes = md.digest();
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        //Get complete hashed password in hex format
		newResource.addResourceAttribute("password", sb.toString());
		resourceRepository.createResource(newResource);
		
		
		
	}
	
	@Test
	@Ignore
	public void testPassword(){
		MessageDigest md=null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md.update("password@098".getBytes());
        //Get the hash's bytes 
        byte[] bytes = md.digest();
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        System.out.println(sb.toString());
        Resource user = resourceRepository.findOne("OTC_User", "{\"lastName\":\"Sankaran\",\"password\":\""+sb.toString()+"\"}");
        if (user.getResourceAttribute("favoriteManufacturers",ArrayList.class) instanceof ArrayList){
        	System.out.println(true);
        }
        Assert.assertNotNull(user);
	}
}
