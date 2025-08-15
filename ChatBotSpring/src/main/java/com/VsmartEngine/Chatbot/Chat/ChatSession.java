package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class ChatSession {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private String sessionId;
    private String sender;   // user email or ID
    private String receiver; // admin email or ID
    private boolean Status;

    // Last time the admin viewed this session. Used to calculate pending/unread messages
    private LocalDateTime lastAdminReadAt;

    private LocalDateTime createdTime;

	public ChatSession() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChatSession(Long id, String sessionId, String sender, String receiver, boolean status,
			LocalDateTime createdTime) {
		super();
		this.id = id;
		this.sessionId = sessionId;
		this.sender = sender;
		this.receiver = receiver;
		Status = status;
		this.createdTime = createdTime;
	}
	
	public ChatSession(String sender, String receiver) {
		super();
		this.sender = sender;
		this.receiver = receiver;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isStatus() {
		return Status;
	}
	public void setStatus(boolean status) {
		Status = status;
	}
    public LocalDateTime getLastAdminReadAt() {
        return lastAdminReadAt;
    }
    public void setLastAdminReadAt(LocalDateTime lastAdminReadAt) {
        this.lastAdminReadAt = lastAdminReadAt;
    }
	
}
