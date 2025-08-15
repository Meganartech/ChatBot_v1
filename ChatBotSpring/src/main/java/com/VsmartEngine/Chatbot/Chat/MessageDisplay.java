package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;

public class MessageDisplay {
	
	private String sessionId;
	private long userid;
	private String username;
	private String useremail;
	private long adminid;
	private String receivername;
	private String adminemail;
    private String Message;
    private boolean Status;
	private LocalDateTime timestamp;
    private long unreadCount;
	public MessageDisplay() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MessageDisplay(String sessionId, long userid, String username, String useremail, long adminid,
			String receivername, String adminemail, String message, boolean status, LocalDateTime timestamp) {
		super();
		this.sessionId = sessionId;
		this.userid = userid;
		this.username = username;
		this.useremail = useremail;
		this.adminid = adminid;
		this.receivername = receivername;
		this.adminemail = adminemail;
		Message = message;
		Status = status;
		this.timestamp = timestamp;
	}
    public long getUnreadCount() {
        return unreadCount;
    }
    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getAdminid() {
		return adminid;
	}
	public void setAdminid(long adminid) {
		this.adminid = adminid;
	}
	public String getReceivername() {
		return receivername;
	}
	public void setReceivername(String receivername) {
		this.receivername = receivername;
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
	public String getUseremail() {
		return useremail;
	}
	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
	public String getAdminemail() {
		return adminemail;
	}
	public void setAdminemail(String adminemail) {
		this.adminemail = adminemail;
	}
	public boolean isStatus() {
		return Status;
	}
	public void setStatus(boolean status) {
		Status = status;
	}

}
