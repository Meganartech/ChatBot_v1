package com.VsmartEngine.Chatbot.WidgetController.ReqRes;

import com.VsmartEngine.Chatbot.WidgetController.Property;
import com.VsmartEngine.Chatbot.WidgetController.PropertyRepo;
import com.VsmartEngine.Chatbot.WidgetController.ReqRes.User;
import com.VsmartEngine.Chatbot.WidgetController.ReqRes.UserLogin;
import com.VsmartEngine.Chatbot.WidgetController.ReqRes.UserRegister;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserAuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final PropertyRepo propertyRepo;

    public UserAuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, PropertyRepo propertyRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.propertyRepo = propertyRepo;
    }

    /** 🔹 Register User */
    public Map<String, Object> register(UserRegister request) {
        Map<String, Object> response = new HashMap<>();
    
        try {
            // Validate email format
            if (!isValidEmail(request.getEmail())) {
                response.put("status", "error");
                response.put("message", "Please provide a valid email address");
                return response;
            }
    
            // Fetch property entity by ID
            Property property = propertyRepo.findById(request.getPropertyId())
                    .orElseThrow(() -> new IllegalArgumentException("Property not found!"));
            
            // Check if email exists (globally unique)
            if (userRepo.existsByEmail(request.getEmail())) {
                response.put("status", "error");
                response.put("message", "Email already registered!");
                return response;
            }
    
            // Check if username exists within the specific property ID
            if (userRepo.existsByUsernameAndPropertyId(request.getUsername(), property.getId())) {
                response.put("status", "error");
                response.put("message", "Username already taken in this property!");
                return response;
            }
    
            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setProperty(property);
    
            // Save user
            userRepo.save(user);
    
            // Send verification email (you'll need to implement this)
            sendVerificationEmail(user);
    
            response.put("status", "success");
            response.put("message", "User registered successfully! Please check your email for verification.");
            response.put("userId", user.getId());
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Registration failed: " + e.getMessage());
        
        }
    
        return response;
    }
    
    private boolean isValidEmail(String email) {
        // Simple email validation regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }
    
    private void sendVerificationEmail(User user) {
        // Implement your email sending logic here
        // Typically includes:
        // 1. Generate verification token
        // 2. Save token in database with expiration
        // 3. Send email with verification link
    }

    /** 🔹 Login User */
    /** 🔹 Login User */
public Map<String, Object> login(UserLogin request) {
    Map<String, Object> response = new HashMap<>();

    // Check if user exists in the specific property
    Optional<User> userOpt = userRepo.findByUsernameAndPropertyId(request.getUsername(), request.getPropertyId());

    if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
        response.put("status", "error");
        response.put("message", "Invalid username, password, or property!");
        return response;
    }

    User user = userOpt.get(); 
    
    response.put("status", "success");
    response.put("message", "Login successful!");
    response.put("username", user.getUsername());
    response.put("propertyId", user.getProperty().getId());

    return response;
}

    /** 🔹 Logout User */
    public Map<String, Object> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logged out successfully");
        return response;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
    public List<User> getUsersByPropertyId(Long propertyId) {
        return userRepo.findUsersByPropertyId(propertyId);
    }
}