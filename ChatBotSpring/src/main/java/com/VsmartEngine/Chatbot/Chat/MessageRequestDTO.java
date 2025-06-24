package com.VsmartEngine.Chatbot.Chat;

public class MessageRequestDTO {
	
	private Long sender;
    private Long receiver;
    private String message;
	public MessageRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MessageRequestDTO(Long sender, Long receiver, String message) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
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
	
}
