package com.VsmartEngine.Chatbot.Chat;

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
	 private Long sessionid;
	 
	 private Long userId;
	 
	 private Long adminId;
	 
	 private Long senderid;
	 
	 private Long receiverid;
	 
	 private boolean status;

	public ChatSession() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChatSession(Long sessionid, Long userId, Long adminId, Long senderid, Long receiverid, boolean status) {
		super();
		this.sessionid = sessionid;
		this.userId = userId;
		this.adminId = adminId;
		this.senderid = senderid;
		this.receiverid = receiverid;
		this.status = status;
	}

	public Long getSessionid() {
		return sessionid;
	}

	public void setSessionid(Long sessionid) {
		this.sessionid = sessionid;
	}

	public Long getSenderid() {
		return senderid;
	}

	public void setSenderid(Long senderid) {
		this.senderid = senderid;
	}

	public Long getReceiverid() {
		return receiverid;
	}

	public void setReceiverid(Long receiverid) {
		this.receiverid = receiverid;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}
	
}
