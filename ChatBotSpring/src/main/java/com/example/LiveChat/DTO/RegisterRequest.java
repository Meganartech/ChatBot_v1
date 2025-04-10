package com.example.LiveChat.DTO;

import com.example.LiveChat.Model.ChatbotType;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private ChatbotType chatbotType; // ✅ Added chatbotType field

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ChatbotType getChatbotType() {
        return chatbotType;
    }

    public void setChatbotType(ChatbotType chatbotType) {
        this.chatbotType = chatbotType;
    }
}
