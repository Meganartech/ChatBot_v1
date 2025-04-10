package com.example.LiveChat.Model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Properties") 
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String adminEmail;

    @Column(unique = true, nullable = false)
    private String propertyName;

    @Column(unique = true, nullable = false)
    private String websiteURL;

    @Column(unique = true, nullable = false)
    private String uniquePropertyId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String widgetScript;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatbotType chatbotType; 

    @Column(nullable = false)
    private String buttonColor; 

    @Column(name = "image_name")
    private String imageName;

    @Lob
    private Long imageOid; // ✅ Store image as OID instead of byte[]

    // Constructors
    public Property() {
        this.uniquePropertyId = UUID.randomUUID().toString();
    }

    public Property(String adminEmail, String propertyName, String websiteURL, String widgetScript, ChatbotType chatbotType, String buttonColor, String imageName, Long imageOid) {
        this.adminEmail = adminEmail;
        this.propertyName = propertyName;
        this.websiteURL = websiteURL;
        this.widgetScript = widgetScript;
        this.chatbotType = chatbotType;
        this.buttonColor = buttonColor;
        this.uniquePropertyId = UUID.randomUUID().toString();
        this.imageName = imageName;
        this.imageOid = imageOid;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

    public String getWebsiteURL() { return websiteURL; }
    public void setWebsiteURL(String websiteURL) { this.websiteURL = websiteURL; }

    public String getUniquePropertyId() { return uniquePropertyId; }
    public void setUniquePropertyId(String uniquePropertyId) { this.uniquePropertyId = uniquePropertyId; }

    public String getWidgetScript() { return widgetScript; }
    public void setWidgetScript(String widgetScript) { this.widgetScript = widgetScript; }

    public ChatbotType getChatbotType() { return chatbotType; }
    public void setChatbotType(ChatbotType chatbotType) { this.chatbotType = chatbotType; }

    public String getButtonColor() { return buttonColor; }
    public void setButtonColor(String buttonColor) { this.buttonColor = buttonColor; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public Long getImageOid() { return imageOid; }
    public void setImageOid(Long imageOid) { this.imageOid = imageOid; }

}
