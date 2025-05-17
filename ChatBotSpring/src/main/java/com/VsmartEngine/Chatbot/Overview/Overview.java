package com.VsmartEngine.Chatbot.Overview;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table
public class Overview {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String propertyname;
	
	private Boolean active;
	
	private String propertyurl;
	
	@Lob
    @Column(name = "image", length = 1000000)
	private byte[] propertyimage;

	public Overview() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Overview(long id, String propertyname, Boolean active, String propertyurl, byte[] propertyimage) {
		super();
		this.id = id;
		this.propertyname = propertyname;
		this.active = active;
		this.propertyurl = propertyurl;
		this.propertyimage = propertyimage;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPropertyname() {
		return propertyname;
	}

	public void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getPropertyurl() {
		return propertyurl;
	}

	public void setPropertyurl(String propertyurl) {
		this.propertyurl = propertyurl;
	}

	public byte[] getPropertyimage() {
		return propertyimage;
	}

	public void setPropertyimage(byte[] propertyimage) {
		this.propertyimage = propertyimage;
	}
	
	

}
