package com.example.LiveChat.Controller;

import com.example.LiveChat.Model.Property;
import com.example.LiveChat.Service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    /** ✅ Extract Token Utility */
    private String extractToken(String authHeader) {
        return (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : authHeader;
    }

    /** ✅ Add Property 
     * @throws SQLException */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addProperty(
            @RequestHeader("Authorization") String token,
            @RequestParam("propertyName") String propertyName,
            @RequestParam("websiteURL") String websiteURL,
            @RequestParam("buttonColor") String buttonColor,
            @RequestParam(value = "widgetScript", required = false) String widgetScript,
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException, SQLException {

        Property property = new Property();
        property.setPropertyName(propertyName);
        property.setWebsiteURL(websiteURL);
        property.setButtonColor(buttonColor);
        if (widgetScript != null) {
            property.setWidgetScript(widgetScript);
        }

        return propertyService.addProperty(extractToken(token), property, imageFile);
    }

    /** ✅ Get All Properties */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllProperties() {
        return propertyService.getAllProperties();
    }

    /** ✅ Get Properties by Admin Email 
     * @throws SQLException */
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getAllPropertiesByAdminEmail(
            @RequestHeader("Authorization") String token) throws SQLException {
        return propertyService.getAllPropertiesByAdminEmail(extractToken(token));
    }

    /** ✅ Get Property by Unique Property ID */
    @GetMapping("/{uniquePropertyId}")
    public ResponseEntity<Map<String, Object>> getPropertyByUniqueId(@PathVariable String uniquePropertyId) {
        return propertyService.getPropertyByUniqueId(uniquePropertyId);
    }

    /** ✅ Update Property */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateProperty(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam(value = "propertyName", required = false) String propertyName,
            @RequestParam(value = "websiteURL", required = false) String websiteURL,
            @RequestParam(value = "buttonColor", required = false) String buttonColor,
            @RequestParam(value = "widgetScript", required = false) String widgetScript,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Property updatedProperty = new Property();
        if (propertyName != null) updatedProperty.setPropertyName(propertyName);
        if (websiteURL != null) updatedProperty.setWebsiteURL(websiteURL);
        if (buttonColor != null) updatedProperty.setButtonColor(buttonColor);
        if (widgetScript != null) updatedProperty.setWidgetScript(widgetScript);

        return propertyService.updateProperty(extractToken(token), id, updatedProperty, imageFile);
    }
}
