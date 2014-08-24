package com.ampark.response;

import java.util.List;

import com.ampark.domain.StringLookup;

public class StringLookupResponse extends GenericResponse {

	private List<StringLookup> lookupArray;

	public List<StringLookup> getLookupArray() {
		return lookupArray;
	}

	public void setLookupArray(List<StringLookup> lookup) {
		this.lookupArray = lookup;
	}
}
