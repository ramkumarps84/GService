package com.ampark.response;

import java.util.List;

public class LoginResponse {

	private Boolean hasError;
	private String  errorMessage;
	private Boolean isAuthorized;
	private String username;
	private String mobile;
	private List<String> favoriteManufacturers;
	
	public Boolean getIsAuthorized() {
		return isAuthorized;
	}
	public void setIsAuthorized(Boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Boolean getHasError() {
		return hasError;
	}
	public void setHasError(Boolean hasError) {
		this.hasError = hasError;
	}
	public List<String> getFavoriteManufacturers() {
		return favoriteManufacturers;
	}
	public void setFavoriteManufacturers(List<String> favoriteManufacturers) {
		this.favoriteManufacturers = favoriteManufacturers;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String userName) {
		this.username = userName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
