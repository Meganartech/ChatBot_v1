package com.example.LiveChat.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuth {
    private String token;
    private String message;
    private String username; // ✅ Added username field

    public UserAuth(String token, String message, String username) {
        this.token = token;
        this.message = message;
        this.username = username;
        
        
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
}
