package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;

public class MessageResponseDTO {
	
	private Long id;
    private Long sender;
    private Long receiver;
    private String message;
    private String role;
    private LocalDateTime timestamp;
	public MessageResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MessageResponseDTO(Long id, Long sender, Long receiver, String message, String role,
			LocalDateTime timestamp) {
		super();
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.role = role;
		this.timestamp = timestamp;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSender() {
		return sender;
	}
	public void setSender(Long sender) {
		this.sender = sender;
	}
	public Long getReceiver() {
		return receiver;
	}
	public void setReceiver(Long receiver) {
		this.receiver = receiver;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
}
