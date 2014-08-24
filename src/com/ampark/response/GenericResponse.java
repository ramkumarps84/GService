package com.ampark.response;

import java.util.List;

import com.ampark.domain.SearchCriteria;


public class GenericResponse {

	private Boolean hasError = false;
	private String errorMessage = "";
	private Integer pageNo;
	private Integer pageSize;
	private List<SearchCriteria> criteriaList;

	
	public Boolean getHasError() {
		return hasError;
	}
	public void setHasError(Boolean hasError) {
		this.hasError = hasError;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	
	public List<SearchCriteria> getCriteriaList() {
		return criteriaList;
	}

	public void setCriteriaList(List<SearchCriteria> criteriaList) {
		this.criteriaList = criteriaList;
	}
	
}
