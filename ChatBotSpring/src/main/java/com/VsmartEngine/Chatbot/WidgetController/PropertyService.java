package com.VsmartEngine.Chatbot.WidgetController;

import com.VsmartEngine.Chatbot.Admin.AdminRegister;
import com.VsmartEngine.Chatbot.Admin.AdminRegisterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class PropertyService {
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    
    private final PropertyRepo propertyRepo;
    private final AdminRegisterRepository adminRepo;
    private final DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    public PropertyService(PropertyRepo propertyRepo, 
                         AdminRegisterRepository adminRepo, 
                         DataSource dataSource) {
        this.propertyRepo = propertyRepo;
        this.adminRepo = adminRepo;
        this.dataSource = dataSource;
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> addProperty(String email, Property property, MultipartFile imageFile) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Normalize and validate email
            String normalizedEmail = normalizeEmail(email);
            // AdminRegister admin = validateAdmin(normalizedEmail);

            // Set admin email and other properties
            property.setAdminEmail(normalizedEmail);
            
            // Handle image upload if present
            handleImageUpload(property, imageFile);

            // Generate and set widget script
            property.setWidgetScript(generateWidgetScript(property));
            
            // Save the property
            Property savedProperty = propertyRepo.save(property);
            
            // Prepare success response
            response.put("status", "success");
            response.put("message", "Property added successfully");
            response.put("propertyId", savedProperty.getId());
            response.put("widgetScript", savedProperty.getWidgetScript());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Admin validation failed: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException | IOException e) {
            logger.error("Error processing image: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error processing image");
        } catch (Exception e) {
            logger.error("Unexpected error adding property: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> updateProperty(String email, Long id, 
                                                            Property updatedProperty, 
                                                            MultipartFile imageFile) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Normalize and validate email
            String normalizedEmail = normalizeEmail(email);
            // validateAdmin(normalizedEmail);

            // Find existing property
            Property property = propertyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

            // Verify ownership
            if (!property.getAdminEmail().equalsIgnoreCase(normalizedEmail)) {
                throw new IllegalArgumentException("Unauthorized: You can only update your own properties");
            }

            // Update property fields
            updatePropertyFields(property, updatedProperty);
            
            // Handle image update if new image provided
            if (imageFile != null && !imageFile.isEmpty()) {
                updatePropertyImage(property, imageFile);
            }

            // Regenerate widget script
            property.setWidgetScript(generateWidgetScript(property));
            
            // Save updated property
            propertyRepo.save(property);
            
            // Prepare success response
            response.put("status", "success");
            response.put("message", "Property updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Validation failed: {}", e.getMessage());
            return buildErrorResponse(response, 
                                   e.getMessage().contains("Unauthorized") ? HttpStatus.FORBIDDEN : HttpStatus.NOT_FOUND, 
                                   e.getMessage());
        } catch (SQLException | IOException e) {
            logger.error("Error updating image: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error updating image");
        } catch (Exception e) {
            logger.error("Unexpected error updating property: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    public ResponseEntity<Map<String, Object>> getAllProperties() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Property> properties = propertyRepo.findAll();
            response.put("status", "success");
            response.put("properties", properties);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching properties: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching properties");
        }
    }

    public ResponseEntity<Map<String, Object>> getAllPropertiesByAdminEmail(String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String normalizedEmail = normalizeEmail(email);
            // validateAdmin(normalizedEmail);

            List<Property> properties = propertyRepo.findAllByAdminEmail(normalizedEmail);
            List<Map<String, Object>> propertyList = new ArrayList<>();
            
            for (Property property : properties) {
                Map<String, Object> propertyData = new HashMap<>();
                propertyData.put("id", property.getId());
                propertyData.put("propertyName", property.getPropertyName());
                propertyData.put("websiteURL", property.getWebsiteURL());
                propertyData.put("uniquePropertyId", property.getUniquePropertyId());
                propertyData.put("buttonColor", property.getButtonColor());
                propertyData.put("widgetScript", property.getWidgetScript());
                propertyData.put("chatbotType", property.getChatbotType());
                propertyData.put("imageName", property.getImageName());
                
                // Add image data if available
                if (property.getImageOid() != null) {
                    propertyData.put("imageData", getImageAsBase64(property.getImageOid()));
                } else {
                    propertyData.put("imageData", null);
                }
                
                propertyList.add(propertyData);
            }
            
            response.put("status", "success");
            response.put("properties", propertyList);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Admin validation failed: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching properties: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching properties");
        }
    }

    public ResponseEntity<Map<String, Object>> getPropertyByUniqueId(String uniquePropertyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Property property = propertyRepo.findByUniquePropertyId(uniquePropertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
            
            response.put("status", "success");
            response.put("property", property);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Property not found: {}", uniquePropertyId);
            return buildErrorResponse(response, HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching property: {}", e.getMessage());
            return buildErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching property");
        }
    }

    // ========== HELPER METHODS ========== //
    
    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        return email.trim().toLowerCase();
    }
    
    // private AdminRegister validateAdmin(String email) {
    //     return adminRepo.findByEmail(email)
    //         .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
    // }
    
    private void handleImageUpload(Property property, MultipartFile imageFile) throws SQLException, IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            Long imageOid = storeImageInDatabase(imageFile);
            property.setImageOid(imageOid);
            property.setImageName(imageFile.getOriginalFilename());
        }
    }
    
    private Long storeImageInDatabase(MultipartFile imageFile) throws SQLException, IOException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            LargeObjectManager lobj = connection.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
            long oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            
            try (LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE)) {
                obj.write(imageFile.getBytes());
            }
            
            connection.commit();
            return oid;
        }
    }
    
    private void updatePropertyFields(Property existing, Property updated) {
        if (updated.getPropertyName() != null) {
            existing.setPropertyName(updated.getPropertyName());
        }
        if (updated.getWebsiteURL() != null) {
            existing.setWebsiteURL(updated.getWebsiteURL());
        }
        if (updated.getButtonColor() != null) {
            existing.setButtonColor(updated.getButtonColor());
        }
        if (updated.getChatbotType() != null) {
            existing.setChatbotType(updated.getChatbotType());
        }
    }
    
    private void updatePropertyImage(Property property, MultipartFile imageFile) throws SQLException, IOException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            LargeObjectManager lobj = connection.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
            
            // Delete old image if exists
            if (property.getImageOid() != null) {
                lobj.delete(property.getImageOid());
            }
            
            // Store new image
            long newOid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            try (LargeObject obj = lobj.open(newOid, LargeObjectManager.WRITE)) {
                obj.write(imageFile.getBytes());
            }
            
            property.setImageOid(newOid);
            property.setImageName(imageFile.getOriginalFilename());
            connection.commit();
        }
    }
    
    private String getImageAsBase64(Long imageOid) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            LargeObjectManager lobj = connection.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
            
            try (LargeObject obj = lobj.open(imageOid, LargeObjectManager.READ)) {
                byte[] imageBytes = obj.read(obj.size());
                return Base64.getEncoder().encodeToString(imageBytes);
            } finally {
                connection.commit();
            }
        } catch (Exception e) {
            logger.error("Error reading image: {}", e.getMessage());
            return null;
        }
    }
    
    private String generateWidgetScript(Property property) {
        return String.format("<script async defer src=\"http://localhost:8080/widget/%s\"></script>", 
                           property.getUniquePropertyId());
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(Map<String, Object> response, 
                                                                 HttpStatus status, 
                                                                 String message) {
        response.put("status", "error");
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}