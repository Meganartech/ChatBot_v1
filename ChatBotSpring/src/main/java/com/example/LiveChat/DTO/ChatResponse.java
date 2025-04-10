package com.example.LiveChat.DTO;

public class ChatResponse {
    private String status;
    private String message;
    private int code;
    private Long chatId;

    public ChatResponse(String status, String message, int code, Long chatId) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.chatId = chatId;
    }

    // Getters & Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
}
