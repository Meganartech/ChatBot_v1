package com.example.LiveChat.Service;

import com.example.LiveChat.Model.Admin;
import com.example.LiveChat.Model.Property;
import com.example.LiveChat.Repository.AdminRepo;
import com.example.LiveChat.Repository.PropertyRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;

import javax.sql.DataSource;  // ✅ Import DataSource
import java.sql.Connection;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Service
public class PropertyService {

    private final PropertyRepo propertyRepo;
    private final JwtService jwtService;
    private final AdminRepo adminRepo;
    private final DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    public PropertyService(PropertyRepo propertyRepo, JwtService jwtService, AdminRepo adminRepo, DataSource dataSource) {
        this.propertyRepo = propertyRepo;
        this.jwtService = jwtService;
        this.adminRepo = adminRepo;
        this.dataSource = dataSource;  // ✅ Assign DataSource
    }

    private String validateTokenAndGetEmail(String token, Map<String, Object> response) {
        if (!jwtService.validateToken(token)) {
            response.put("status", "error");
            response.put("message", "Invalid or expired token.");
            return null;
        }
        return jwtService.extractEmail(token);
    }

    /** ✅ Add Property (With Image Using PostgreSQL Large Object) */
    @Transactional
    public ResponseEntity<Map<String, Object>> addProperty(String token, Property property, MultipartFile imageFile) throws IOException {
        Map<String, Object> response = new HashMap<>();
        String email = validateTokenAndGetEmail(token, response);
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        Optional<Admin> adminOptional = adminRepo.findByEmail(email);
        if (adminOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Admin not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Admin admin = adminOptional.get();
        property.setChatbotType(admin.getChatbotType());
        property.setAdminEmail(email);

        // ✅ Store Image Using PostgreSQL Large Object API
        if (imageFile != null && !imageFile.isEmpty()) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                LargeObjectManager lobj = connection.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
                long oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
                try (LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE)) {
                    obj.write(imageFile.getBytes());
                }
                property.setImageOid(oid);
                property.setImageName(imageFile.getOriginalFilename());
                
                connection.commit();
            } catch (SQLException e) {
                response.put("status", "error");
                response.put("message", "Error saving image to database: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        // ✅ Generate Unique Widget Script Before Saving
        String widgetScript = generateWidgetScript(property);
        property.setWidgetScript(widgetScript); // 🔥 Ensure this is set before saving

        propertyRepo.save(property);
        
        response.put("status", "success");
        response.put("widgetScript", widgetScript);
        response.put("chatbotType", admin.getChatbotType());
        return ResponseEntity.ok(response);
    }

    private String generateWidgetScript(Property property) {
        return "<script async defer src=\"http://localhost:8080/widget/" + property.getUniquePropertyId() + "\"></script>";
    }

    /** ✅ Update Property (With Image Update Support) */
    @Transactional
    public ResponseEntity<Map<String, Object>> updateProperty(String token, Long id, Property updatedProperty, MultipartFile imageFile) throws IOException {
        Map<String, Object> response = new HashMap<>();
        String email = validateTokenAndGetEmail(token, response);
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        Optional<Property> optionalProperty = propertyRepo.findById(id);
        if (optionalProperty.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Property ID not found");
            return ResponseEntity.badRequest().body(response);
        }

        Property property = optionalProperty.get();

        // 🔐 Ensure only the owner can update
        if (!property.getAdminEmail().equals(email)) {
            response.put("status", "error");
            response.put("message", "Unauthorized: You can only update your own properties.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // ✅ Update property details if provided
        if (updatedProperty.getPropertyName() != null) property.setPropertyName(updatedProperty.getPropertyName());
        if (updatedProperty.getWebsiteURL() != null) property.setWebsiteURL(updatedProperty.getWebsiteURL());
        if (updatedProperty.getButtonColor() != null) property.setButtonColor(updatedProperty.getButtonColor());

        // 🔄 Regenerate widget script if needed
        property.setWidgetScript(generateWidgetScript(property));

        // ✅ Handle Image Update
        if (imageFile != null && !imageFile.isEmpty()) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                LargeObjectManager lobj = connection.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();

                // ❌ Delete old image if exists
                if (property.getImageOid() != null) {
                    lobj.delete(property.getImageOid());
                }

                // 🆕 Upload new image
                long newOid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
                try (LargeObject obj = lobj.open(newOid, LargeObjectManager.WRITE)) {
                    obj.write(imageFile.getBytes());
                }
                property.setImageOid(newOid);
                property.setImageName(imageFile.getOriginalFilename());

                connection.commit();
            } catch (SQLException e) {
                response.put("status", "error");
                response.put("message", "Error updating image: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        propertyRepo.save(property);
        response.put("status", "success");
        response.put("message", "Property updated successfully");
        return ResponseEntity.ok(response);
    }

    /** ✅ Get All Properties */
    public ResponseEntity<Map<String, Object>> getAllProperties() {
        Map<String, Object> response = new HashMap<>();
        List<Property> properties = propertyRepo.findAll();
        response.put("status", "success");
        response.put("properties", properties);
        return ResponseEntity.ok(response);
    }

    /** ✅ Get Properties by Admin Email 
     * @throws SQLException */
    public ResponseEntity<Map<String, Object>> getAllPropertiesByAdminEmail(String token) throws SQLException {
        Map<String, Object> response = new HashMap<>();
        String email = validateTokenAndGetEmail(token, response);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Fetch properties from the database based on the email
        List<Property> properties = propertyRepo.findAllByAdminEmail(email);

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
            propertyData.put("imageName", property.getImageName()); // Store image name if needed

            // Check if the property has an image OID
            if (property.getImageOid() != null) {
                System.out.println("Image OID found: " + property.getImageOid());
            } else {
                System.out.println("No Image OID for property: " + property.getPropertyName());
            }

            // Convert image from OID to Base64
            if (property.getImageOid() != null) {
                Connection connection = dataSource.getConnection();
				try (connection) {
                    // Disable auto-commit mode for large object operations
                    connection.setAutoCommit(false);

                    // Get the LargeObjectManager to handle the large object
                    LargeObjectManager lobj = connection.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();

                    // Open the large object using the OID and read the data
                    try (LargeObject obj = lobj.open(property.getImageOid(), LargeObjectManager.READ)) {
                        byte[] imageBytes = obj.read(obj.size()); // Read the image bytes
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes); // Convert bytes to Base64 string
                        propertyData.put("imageData", base64Image); // Add the Base64 image string to the property data
                    }

                    // Commit the transaction after reading the large object
                    connection.commit();
                } catch (Exception e) {
                    // Rollback in case of error and set imageData to null
                    try {
                        connection.rollback();
                    } catch (SQLException rollbackException) {
                        // Handle rollback exception if any
                        System.err.println("Error during rollback: " + rollbackException.getMessage());
                    }
                    propertyData.put("imageData", null); // Set imageData to null if there was an error
                    System.err.println("Error processing image: " + e.getMessage());
                } finally {
                    // Restore auto-commit mode to true after operation
                    try {
                        connection.setAutoCommit(true);
                    } catch (SQLException e) {
                        System.err.println("Error restoring auto-commit: " + e.getMessage());
                    }
                }
            } else {
                propertyData.put("imageData", null); // No image if the OID is null
            }

            // Add the property data to the list
            propertyList.add(propertyData);
        }

        // Prepare the final response
        response.put("status", "success");
        response.put("properties", propertyList);

        // Log and return the response
        System.out.println("Response: " + response);
        return ResponseEntity.ok(response);
    }

    /** ✅ Get Property by Unique ID */
    public ResponseEntity<Map<String, Object>> getPropertyByUniqueId(String uniquePropertyId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Property> property = propertyRepo.findByUniquePropertyId(uniquePropertyId);

        if (property.isPresent()) {
            response.put("status", "success");
            response.put("property", property.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Property ID not found");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
