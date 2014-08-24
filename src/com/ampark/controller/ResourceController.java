package com.ampark.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ampark.domain.SearchCriteria;
import com.ampark.domain.StringLookup;
import com.ampark.response.GenericResponse;
import com.ampark.response.ResourceResponse;
import com.ampark.response.ResourcesResponse;
import com.ampark.response.StringLookupResponse;
import com.ampark.service.ResourceService;
import com.mongodb.DBObject;

@Controller
public class ResourceController {

	static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
	
	@Autowired
	private  ResourceService resourceService;
	
	
	@RequestMapping("/home")
    public String loadTestPage(Model m) {
		m.addAttribute("name", "Grigora");
        return "home";
    }
	
	@RequestMapping("/index")
    public String loadIndexPage(Model m) {
        return "index";
    }
	
    /**
     * Fetches all the records from the document specified by the request parameter 'resourceType'
     * 
     * @param allParams
     * @return
     * @usage http://localhost:8080/grigora/findAllResources?resourceType=OTC_Location&isMediaType=false&page=2&limit=2&searchString=tn&searchType=text
     * @usage http://localhost:8080/grigora/findAllResources?resourceType=OTC_Location&isMediaType=false&page=1&limit=3&searchType=field&searchString=nnai&searchKey=City
     */
    @RequestMapping(value="findAllResources")
    public @ResponseBody ResourcesResponse findAllResources(@RequestParam Map<String,String> allParams){
    	logger.debug("Calling findAllResources on resource"+allParams.get("resourceType")+" with parameters "+StringUtils.join(allParams));
    	ResourcesResponse response = new ResourcesResponse();
    	if(!allParams.containsKey("page") || !allParams.containsKey("limit")){
    		allParams.put("page", "1");
    		allParams.put("limit", "10");
    	}
    	response.setResources(resourceService.findAll(allParams));
    	constructResponse(allParams, response);
    	return response;
    }
    
    @RequestMapping(value="findOneResource")
    public @ResponseBody ResourceResponse findOneResource(@RequestParam Map<String,String> allParams){
    	logger.debug("Calling findOneResources on resource"+allParams.get("resourceType")+" with parameters "+StringUtils.join(allParams));
    	ResourceResponse response = new ResourceResponse();
    	if(!allParams.containsKey("searchType") || !allParams.containsKey("searchString")){
    		response.setHasError(true);
    		response.setErrorMessage("Should provide atleast one serch criteria.");
    	}else{
    		response.setResource(resourceService.findOne(allParams));
    	}
    	
    	constructResponse(allParams, response);
    	return response;
    }
    
    /**
     * 
     * @param allParams
     * @return
     * @usage http://localhost:8080/grigora/findAllDocuments?resourceType=OTC_Location&isMediaType=true&page=1&limit=3
     */
    @RequestMapping(value="findAllDocuments")
    public @ResponseBody ResourcesResponse findAllDocuments(@RequestParam Map<String,String> allParams){
    	logger.debug("Calling findAllDocuments on resource"+allParams.get("resourceType")+" with parameters "+StringUtils.join(allParams));
    	ResourcesResponse response = new ResourcesResponse();
    	if(!allParams.containsKey("page") || !allParams.containsKey("limit")){
    		allParams.put("page", "1");
    		allParams.put("limit", "10");
    	}
    	response.setResources(resourceService.findAllDocuments(allParams));
    	constructResponse(allParams, response);
    	return response;
    }
    
    /**
     * 
     * @param allParams
     * @return
     * @usage http://localhost:8080/grigora/findAllDocuments?resourceType=OTC_Location&isMediaType=true&page=1&limit=3
     */
    @RequestMapping(value="findOneDocument")
    public @ResponseBody ResourceResponse findOneDocument(@RequestParam Map<String,String> allParams){
    	logger.debug("Calling findAllDocuments on resource"+allParams.get("resourceType")+" with parameters "+StringUtils.join(allParams));
    	ResourceResponse response = new ResourceResponse();
    	response.setResource(resourceService.findOneDocument(allParams));
    	constructResponse(allParams, response);
    	return response;
    }
    
    /**
     * 
     * @param allParams
     * @return
     * @usage http://localhost:8080/grigora/lookupDistinct?resourceType=OTC_Location&isMediaType=false&page=1&limit=50&attributeName=City
     */
    @RequestMapping(value="lookupDistinct")
    public @ResponseBody StringLookupResponse getDistinctValueForAttr(@RequestParam Map<String,String> allParams){
    	logger.debug("Calling getDistinctValueForAttr on resource"+allParams.get("resourceType")+" with parameters "+StringUtils.join(allParams));
    	List<StringLookup> lookup =  resourceService.getDistinctValueForAttr(allParams);
    	StringLookupResponse response = new StringLookupResponse();
    	response.setLookupArray(lookup);
    	return response;
    }
    
    @RequestMapping(value="getAttrValue")
    public @ResponseBody StringLookupResponse getAttrValue(@RequestParam Map<String,String> allParams){
    	logger.debug("Calling getAttrValue on resource"+allParams.get("resourceType")+" with parameters "+StringUtils.join(allParams));
    	List<StringLookup> lookup =  resourceService.getAttrValue(allParams);
    	StringLookupResponse response = new StringLookupResponse();
    	response.setLookupArray(lookup);
    	return response;
    }
    
    @RequestMapping(value="updateResource")
    public @ResponseBody DBObject updateReource(@RequestParam Map<String,String> allParams){
    	logger.debug("Calling findAllDocuments on resource"+allParams.get("resourceType")+" with parameters "+StringUtils.join(allParams));
    	return resourceService.findAndUpdateResourceByID(allParams);
    }
    
    @RequestMapping(value="addToFavorites",method=RequestMethod.POST)
    public @ResponseBody DBObject addToFavorites(@RequestParam Map<String,String> allParams){
    	logger.debug("Calling addToFavorites on resource"+allParams.get("resourceType")+" with parameters "+StringUtils.join(allParams));
    	return resourceService.addToFavorites(allParams);
    }
    
    
    private void constructResponse(Map<String,String> allParams,GenericResponse response){
    	List<SearchCriteria> requestCriteria= new ArrayList<SearchCriteria>(1);
    	requestCriteria.add(new SearchCriteria(allParams.get("searchType"), allParams.get("searchKey"), allParams.get("searchString")));
    	response.setCriteriaList(requestCriteria);   
    	if(allParams.containsKey("page"))
    	response.setPageNo(Integer.parseInt(allParams.get("page").toString()));
    	if(allParams.containsKey("limit"))
    	response.setPageSize(Integer.parseInt(allParams.get("limit").toString()));
    }
    
}