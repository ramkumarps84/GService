package com.ampark.response;

import com.ampark.domain.Resource;

public class ResourceResponse extends GenericResponse {

	private Resource resource;
	
	
	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
