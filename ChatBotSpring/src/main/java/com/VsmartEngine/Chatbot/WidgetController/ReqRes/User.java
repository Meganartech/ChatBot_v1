package com.VsmartEngine.Chatbot.WidgetController.ReqRes;

import com.VsmartEngine.Chatbot.WidgetController.Property;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

	@Column(nullable = false, unique = true)
	private String email;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}
   public String getEmail() {
		return email;
		}
	public void setEmail(String email) {
		this.email = email;
	}
    
}