package com.example.LiveChat.Service;

import com.example.LiveChat.Model.User;
import com.example.LiveChat.Model.Property;
import com.example.LiveChat.Repository.PropertyRepo;
import com.example.LiveChat.Repository.UserRepo;
import com.example.LiveChat.DTO.UserLogin;
import com.example.LiveChat.DTO.UserRegister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserAuthService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final PropertyRepo propertyRepo;


    public UserAuthService(UserRepo userRepo, JwtService jwtService, PasswordEncoder passwordEncoder, PropertyRepo propertyRepo) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
		this.propertyRepo = propertyRepo;
    }

    /** 🔹 Register User */
    public Map<String, Object> register(UserRegister request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch property entity by ID
            Property property = propertyRepo.findById(request.getPropertyId())
                    .orElseThrow(() -> new IllegalArgumentException("Property not found!"));
            
            // Check if username exists within the specific property ID
            if (userRepo.existsByUsernameAndPropertyId(request.getUsername(), property.getId())) {
                response.put("status", "error");
                response.put("message", "Username already taken in this property!");
                return response;
            }

            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setProperty(property);

            // Save user
            userRepo.save(user);

            response.put("status", "success");
            response.put("message", "User registered successfully!");
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Something went wrong during registration!");
        }

        return response;
    }


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
        
        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername());

        response.put("status", "success");
        response.put("message", "Login successful!");
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("propertyId", user.getProperty().getId()); // Return only necessary property info

        return response;
    }


    /** 🔹 Logout User */
    public Map<String, Object> logout(String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Invalid Authorization header");
            return response;
        }

        String token = authHeader.substring(7);
        jwtService.invalidateToken(token); // Properly invalidates token

        response.put("status", "success");
        response.put("message", "Logged out successfully");
        return response;
    }

    /** 🔹 Validate Token */
    public Map<String, Object> validateToken(String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Invalid Authorization header");
            return response;
        }

        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            response.put("status", "error");
            response.put("message", "Invalid or expired token");
            return response;
        }

        String username = jwtService.extractEmail(token); // ✅ Corrected method call
        response.put("status", "success");
        response.put("message", "Token is valid");
        response.put("username", username); // ✅ Returning the correct extracted username
        return response;
    }
    
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
    public List<User> getUsersByPropertyId(Long propertyId) {
        return userRepo.findUsersByPropertyId(propertyId);
    }
}
