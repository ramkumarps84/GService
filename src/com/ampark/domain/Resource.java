package com.ampark.domain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;

@Document(collection="resources")
public class Resource {


	private String resourceType;
	
	private Boolean isMediaType=false;

	@Id
	private String resourceId;
	
	private Map<String,Object> resourceDetail = new HashMap<String,Object>();
	
	@JsonIgnoreProperties
	private Map<String,Resource> resourceDependents  = new HashMap<String,Resource>();
	
	@JsonIgnoreProperties
	private Map<String,String> resourceDependentsId = new HashMap<String,String>();
	
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public void addResourceAttribute(String key,Object value){
		
		if(value instanceof Resource){
			resourceDependents.put(key, (Resource)value);
		}else if(key.contains("_id")){
			resourceDependentsId.put(key, value.toString());
		}else{
			resourceDetail.put(key, value);
		}
	}
	
	public Object getResourceAttribute(String key, Class clazz){
		
		if(clazz.equals(Resource.class)){
			if(resourceDependents.containsKey(key))
					return resourceDependents.get(key);
		}else{
			if(resourceDetail.containsKey(key))
					return resourceDetail.get(key);
		}
		return null;
	}
	
	
	public Map<String,Object> getAllDirectAttributes(){
		return resourceDetail;
	}
	
	public Map<String,Resource> getAllResourceDependents(){
		return resourceDependents;
	}
	
	
	public Map<String,String> getAllResourceDependentsIds() {
		return resourceDependentsId; 
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("{\"resourceType\":\"");
		builder.append(this.getResourceType());
		if(this.getResourceId()!=null){
		builder.append("\",\"_id\":\"");
		builder.append(this.getResourceId());
		}
		//builder.append("\",\"allDirectAttributes\":{");
		builder.append("\",");
		String attrJSON = new Gson().toJson(this.getAllDirectAttributes());
		builder.append(attrJSON.substring(1, attrJSON.length()-1));
		builder.append("}");
		//builder.append("{\"isMediaType\":\"");
		//builder.append(this.getIsMediaType());
		//builder.append("\"");
		return builder.toString();
	}
	public Boolean getIsMediaType() {
		return isMediaType;
	}
	
	public void setIsMediaType(Boolean isMediaType) {
		this.isMediaType = isMediaType;
	}
	
	public void addResourceAttributes(Map map) {
		this.resourceDetail.putAll(map);
		
	}

	
	
}
