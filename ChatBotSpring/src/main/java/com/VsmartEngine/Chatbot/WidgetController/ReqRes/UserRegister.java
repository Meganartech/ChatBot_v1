package com.VsmartEngine.Chatbot.WidgetController.ReqRes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegister {
    private String username;
    private String password;
	private String email;
    private Long propertyId;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
