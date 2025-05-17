package com.VsmartEngine.Chatbot.WidgetController.ReqRes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuth {
    private String token;
    private String message;
	private String emailId;
    private String username; // ✅ Added username field

    public UserAuth(String token,String emailId, String message, String username) {
        this.token = token;
        this.message = message;
        this.username = username;
		this.emailId = emailId;
        
        
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
}
