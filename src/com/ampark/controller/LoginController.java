package com.ampark.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ampark.domain.Resource;
import com.ampark.response.LoginResponse;
import com.ampark.service.ResourceService;

@Controller
public class LoginController {

	static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private  ResourceService resourceService;
	
	 @RequestMapping(value="doLogin")
	    public @ResponseBody LoginResponse doLogin(@RequestParam Map<String,String> allParams){
	    	LoginResponse response = new LoginResponse();
	    	Resource user = resourceService.getResourceForCredentials(allParams.get("username"),allParams.get("mobile"));
	    	response.setIsAuthorized(user!=null?true:false);
	    	if(user!=null){
	    	response.setUsername(user.getResourceAttribute("username", String.class).toString());
	    	response.setMobile(user.getResourceAttribute("mobile", String.class).toString());
	    	response.setFavoriteManufacturers((List<String>) user.getResourceAttribute("favoriteManufacturers", List.class));
	    	}
	    	return response;
	    	
	    }
	 
	 @RequestMapping(value="doRegister",method=RequestMethod.POST)
	    public @ResponseBody LoginResponse doRegister(@RequestParam Map<String,String> allParams){
	    	LoginResponse response = new LoginResponse();
	    	Resource res = new Resource();
			res.setResourceType(allParams.get("resourceType"));
			res.addResourceAttributes(allParams);
			res.addResourceAttribute("favoriteManufacturers",new ArrayList<String>());
			
			Map<String,String> queryParams = new HashMap<String,String>();
			queryParams.put("resourceType", "OTC_User");
			queryParams.put("searchType", "field");
			queryParams.put("searchKey", "username");
			queryParams.put("searchString",allParams.get("username"));
			Resource user = resourceService.findOne(queryParams);
			if(user == null)
				try{
	    	user = resourceService.createResource(res);
	    	response.setIsAuthorized(user!=null?true:false);
	    	response.setUsername(user.getResourceAttribute("username", String.class).toString());
	    	response.setFavoriteManufacturers((List<String>) user.getResourceAttribute("favoriteManufacturers", List.class));
				}catch(Exception e){
					response.setIsAuthorized(false);
				}
	    	return response;
	    	
	    }
}
