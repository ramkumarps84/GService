package com.ampark.domain;


public class SearchCriteria {

	private String searchKey;
	private Object searchString;
	private String searchType;
	
	
	public SearchCriteria(String searchType,String searchKey, Object searchString) {
		// TODO Auto-generated constructor stub
		this.setSearchType(searchType);
		this.searchKey = searchKey;
		this.searchString = searchString;
	}
	public Object getsearchString() {
		return searchString;
	}
	public void setsearchString(Object searchString) {
		this.searchString = searchString;
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	
}
