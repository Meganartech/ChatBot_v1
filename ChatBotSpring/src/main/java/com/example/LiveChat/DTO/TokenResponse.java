package com.example.LiveChat.DTO;

public class TokenResponse {
    private String message;
    private String email;

    public TokenResponse(String message, String email) {
        this.message = message;
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }
}
