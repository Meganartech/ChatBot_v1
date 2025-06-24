package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class ChatMessage {
	
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;

	 @Column(nullable = false)
	 private Long sender;

	 @Column(nullable = false)
	 private Long receiver;

	 @Column(nullable = false, columnDefinition = "TEXT")
	 private String message;

	 @Column(nullable = false)
	 private Long sessionId;
	 
	 @Column(nullable = false)
	 private String role;

	 @CreationTimestamp
	 @Column(nullable = false, updatable = false)
	 private LocalDateTime timestamp;

	public ChatMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChatMessage(Long id, Long sender, Long receiver, String message, Long sessionId, String role,
			LocalDateTime timestamp) {
		super();
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.sessionId = sessionId;
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

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
