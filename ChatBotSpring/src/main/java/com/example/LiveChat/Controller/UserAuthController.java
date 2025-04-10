package com.example.LiveChat.Controller;

import com.example.LiveChat.DTO.UserLogin;
import com.example.LiveChat.DTO.UserRegister;
import com.example.LiveChat.Model.User;
import com.example.LiveChat.Service.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/auth")
@CrossOrigin
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    /** 🔹 Register User */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserRegister request) {
        Map<String, Object> response = userAuthService.register(request);
        return ResponseEntity.ok(response);
    }

    /** 🔹 Login User */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLogin request) {
        Map<String, Object> response = userAuthService.login(request);
        return ResponseEntity.ok(response);
    }

    /** 🔹 Logout User */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = userAuthService.logout(authHeader);
        return ResponseEntity.ok(response);
    }

    /** 🔹 Validate Token */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = userAuthService.validateToken(authHeader);
        return ResponseEntity.ok(response);
    }

    /** 🔹 Get All Users */
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userAuthService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<User>> getUsersByPropertyId(@PathVariable Long propertyId) {
        List<User> users = userAuthService.getUsersByPropertyId(propertyId);
        return ResponseEntity.ok(users);
    }
}
