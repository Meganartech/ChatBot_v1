package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class MessageDTO {
	
	private Long sessionId;
	
	private Long senderid;
	
	private String senderName;
	
	private Long receiver;
	
	private String receiverName;
	
	private String Message;
	
	private LocalDateTime timestamp;

	public MessageDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MessageDTO(Long sessionId, Long senderid, String senderName, Long receiver, String receiverName,
			String message, LocalDateTime timestamp) {
		super();
		this.sessionId = sessionId;
		this.senderid = senderid;
		this.senderName = senderName;
		this.receiver = receiver;
		this.receiverName = receiverName;
		Message = message;
		this.timestamp = timestamp;
	}

	public Long getSenderid() {
		return senderid;
	}

	public void setSenderid(Long senderid) {
		this.senderid = senderid;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Long getReceiver() {
		return receiver;
	}

	public void setReceiver(Long receiver) {
		this.receiver = receiver;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	
}
