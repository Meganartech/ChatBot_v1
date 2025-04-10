package com.example.LiveChat.Service;

import com.example.LiveChat.DTO.LoginRequest;
import com.example.LiveChat.DTO.OtpRequest;
import com.example.LiveChat.DTO.RegisterRequest;
import com.example.LiveChat.Model.Admin;
import com.example.LiveChat.Repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    private final Map<String, RegisterRequest> tempStorage = new HashMap<>();

    public Map<String, Object> register(RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        // ✅ Check if the email is already registered
        Optional<Admin> existingAdmin = adminRepo.findByEmail(request.getEmail());
        if (existingAdmin.isPresent()) {
            response.put("status", "error");
            response.put("message", "Email already registered. Please login or verify your OTP.");
            return response;
        }

        // ✅ Validate chatbot type (Ensure it's not null)
        if (request.getChatbotType() == null) {
            response.put("status", "error");
            response.put("message", "Please select a chatbot type.");
            return response;
        }

        // ✅ Store user details temporarily until OTP verification
        tempStorage.put(request.getEmail(), request);
        
        // ✅ Send OTP for verification
        otpService.sendOtp(request.getEmail());

        response.put("status", "success");
        response.put("message", "OTP sent to email. Please verify to complete registration.");
        response.put("email", request.getEmail());
        response.put("chatbotType", request.getChatbotType().name()); // Return selected chatbot type

        return response;
    }


    public Map<String, Object> verifyOtp(OtpRequest request) {
        Map<String, Object> response = new HashMap<>();

        // ✅ Step 1: Validate OTP
        if (!otpService.isOtpValid(request.getEmail(), request.getOtp())) {
            response.put("status", "error");
            response.put("message", "Invalid or expired OTP.");
            return response;
        }

        // ✅ Step 2: Retrieve stored registration details
        RegisterRequest storedRequest = tempStorage.get(request.getEmail());
        if (storedRequest == null) {
            response.put("status", "error");
            response.put("message", "No registration found for this email.");
            return response;
        }

        // ✅ Step 3: Ensure chatbotType is not null
        if (storedRequest.getChatbotType() == null) {
            response.put("status", "error");
            response.put("message", "Chatbot type is missing. Please re-register.");
            return response;
        }

        // ✅ Step 4: Create and save new admin account
        Admin newAdmin = new Admin();
        newAdmin.setName(storedRequest.getName());
        newAdmin.setEmail(storedRequest.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(storedRequest.getPassword()));
        newAdmin.setVerified(true);
        newAdmin.setChatbotType(storedRequest.getChatbotType()); // ✅ Set chatbot type

        adminRepo.save(newAdmin); // ✅ Save to database

        // ✅ Step 5: Remove temporary storage
        tempStorage.remove(request.getEmail());

        response.put("status", "success");
        response.put("message", "Email verified successfully. You can now log in.");
        return response;
    }

    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<Admin> adminOptional = adminRepo.findByEmail(request.getEmail());

        if (adminOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Invalid credentials");
            return response;
        }

        Admin admin = adminOptional.get();
        if (!admin.isVerified()) {
            response.put("status", "error");
            response.put("message", "Account not verified. Please verify your email.");
            return response;
        }

        if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            String token = jwtService.generateToken(request.getEmail());
            response.put("status", "success");
            response.put("message", "Login successful.");
            response.put("token", token);
            response.put("name", admin.getName());
            response.put("email", admin.getEmail());
            response.put("chatbot_type", admin.getChatbotType());// ✅ Added admin's email in response
            return response;
        }

        response.put("status", "error");
        response.put("message", "Invalid credentials");
        return response;
    }

    public Map<String, Object> logout(String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Invalid Authorization header");
            return response;
        }

        String token = authHeader.substring(7);
        jwtService.invalidateToken(token);

        response.put("status", "success");
        response.put("message", "Logged out successfully");
        return response;
    }

    public Map<String, Object> validateToken(String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Invalid Authorization header");
            return response;
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtService.validateToken(token);

        if (isValid) {
            String email = jwtService.extractEmail(token);
            response.put("status", "success");
            response.put("message", "Token is valid");
            response.put("email", email);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid or expired token");
        }

        return response;
    }

    public Map<String, Object> resendOtp(Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email is required!");
            return response;
        }

        if (adminRepo.findByEmail(email).isPresent()) {
            response.put("status", "error");
            response.put("message", "User already registered. Please log in.");
            return response;
        }

        otpService.resendOtp(email);
        response.put("status", "success");
        response.put("message", "New OTP sent successfully!");
        return response;
    }

}
