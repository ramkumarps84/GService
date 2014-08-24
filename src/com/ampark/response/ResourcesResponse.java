package com.ampark.response;

import java.util.List;

import com.ampark.domain.Resource;

public class ResourcesResponse extends GenericResponse {

	private List<Resource> resources;
	
	
	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
}
