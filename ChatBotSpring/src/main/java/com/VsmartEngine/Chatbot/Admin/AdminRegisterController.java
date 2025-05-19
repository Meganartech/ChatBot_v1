package com.VsmartEngine.Chatbot.Admin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.VsmartEngine.Chatbot.Departments.Department;
import com.VsmartEngine.Chatbot.Departments.DepartmentRepository;
import com.VsmartEngine.Chatbot.MailConfiguration.EmailService;
import com.VsmartEngine.Chatbot.TokenGeneration.JwtUtil;

import jakarta.transaction.Transactional;

@CrossOrigin()
@RequestMapping("/chatbot")
@Controller
public class AdminRegisterController {
	
	@Autowired
	private AdminRegisterRepository adminregisterrepository;
	
	@Autowired
	private DepartmentRepository departmentrepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private RoleRepository rolerepository;
	
	@Autowired
	private EmailService emailservice;
	
	@PostMapping("/register")
	public ResponseEntity<?> register(
	        @RequestParam("username") String username,
	        @RequestParam("email") String email,
	        @RequestParam("password") String password,
	        @RequestParam(value = "role", required = false) String role,
	        Principal principal) {

	    try {
	        long count = adminregisterrepository.count();

	        // Find the existing invited user by email
	        Optional<AdminRegister> existingUserOpt = adminregisterrepository.findByEmail(email);
	        
	        if (existingUserOpt.isPresent()) {
	            AdminRegister existingUser = existingUserOpt.get();

	            // If user already has a username and password, prevent duplicate registration
	            if (existingUser.getUsername() != null && existingUser.getPassword() != null) {
	                return ResponseEntity.badRequest().body("Email is already registered.");
	            }

	            existingUser.setUsername(username);
	            existingUser.setPassword(new BCryptPasswordEncoder().encode(password));
	            existingUser.setStatus(true); // Activate the user

	            adminregisterrepository.save(existingUser);
	            return ResponseEntity.ok("User registered successfully.");
	        }

	        // Handle first admin case
	        if (count == 0) {
	            AdminRegister newUser = new AdminRegister();
	            newUser.setUsername(username);
	            newUser.setEmail(email);
	            newUser.setPassword(new BCryptPasswordEncoder().encode(password));

	            Role adminRole = rolerepository.findByRole("ADMIN")
	                    .orElseThrow(() -> new RuntimeException("Role 'ADMIN' not found"));

	            newUser.setRole(adminRole);
	            newUser.setStatus(true);

	            adminregisterrepository.save(newUser);
	            return ResponseEntity.ok("First Admin registered successfully.");
	        }

	        return ResponseEntity.badRequest().body("Invalid registration attempt. Please check your invitation.");

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error: " + e.getMessage());
	    }
	}
	
	
//	@PostMapping("/register")
//	public ResponseEntity<?> register(
//	        @RequestParam("username") String username,
//	        @RequestParam("email") String email,
//	        @RequestParam("password") String password,
//	        @RequestParam(value = "role", required = false) String role,
//	        Principal principal) {
//
//	    try {
//	        boolean adminCount = adminregisterrepository.existsAdminByRole();
//	        
//	        System.out.println("count"+adminCount);
//
//	        // If no admin exists, allow first admin registration
//	        if (adminCount) {
//	            AdminRegister firstAdmin = new AdminRegister();
//	            firstAdmin.setUsername(username);
//	            firstAdmin.setEmail(email);
//	            firstAdmin.setPassword(new BCryptPasswordEncoder().encode(password));
//	            firstAdmin.setStatus(true);
//
//	            Role adminRole = rolerepository.findByRole("ADMIN")
//	                    .orElseThrow(() -> new RuntimeException("Role 'ADMIN' not found"));
//
//	            firstAdmin.setRole(adminRole);
//
//	            adminregisterrepository.save(firstAdmin);
//	            return ResponseEntity.ok("First Admin registered successfully.");
//	        }
//
//	        // Normal invited-user registration flow
//	        Optional<AdminRegister> existingUserOpt = adminregisterrepository.findByEmail(email);
//	        if (existingUserOpt.isPresent()) {
//	            AdminRegister user = existingUserOpt.get();
//	            if (user.getUsername() != null && user.getPassword() != null) {
//	                return ResponseEntity.badRequest().body("Email is already registered.");
//	            }
//
//	            user.setUsername(username);
//	            user.setPassword(new BCryptPasswordEncoder().encode(password));
//	            user.setStatus(true);
//
//	            adminregisterrepository.save(user);
//	            return ResponseEntity.ok("User registered successfully.");
//	        }
//
//	        return ResponseEntity.badRequest().body("Invalid registration attempt. Please check your invitation.");
//
//	    } catch (Exception e) {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body("Error: " + e.getMessage());
//	    }
//	}


	@PostMapping("/login")
	public ResponseEntity<?> AdminLogin(@RequestBody Map<String, String> loginRequest){
		try {
	        String email = loginRequest.get("email");
	        String password = loginRequest.get("password");
	        Optional<AdminRegister> admin = adminregisterrepository.findByEmail(email);
	        if (!admin.isPresent()) {
	            // User with the provided email doesn't exist
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
	        }
	        AdminRegister adminregister = admin.get();
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        
	        if (!passwordEncoder.matches(password, adminregister.getPassword())) {
	            // Incorrect password
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Incorrect password\"}");
	        }
	        String role = adminregister.getRole().getRole();
	        System.out.println(role);
	        String jwtToken = jwtUtil.generateToken(email,role);
	        Map<String, Object> responseBody = new HashMap<>();
	        responseBody.put("token", jwtToken);
	        responseBody.put("message", "Login successful");
	        responseBody.put("name", adminregister.getUsername());
	        responseBody.put("email", adminregister.getEmail());
	        responseBody.put("userId", adminregister.getId());
	        responseBody.put("role", adminregister.getRole().getRole());	        
	        System.out.println("token" + jwtToken);
	        
	        return ResponseEntity.ok(responseBody);

		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}
	
	@GetMapping("/getAllAdmin")
	public ResponseEntity<List<AdminRegister>> getAllUser() {
        try {
            // Fetch all users from the repository
            List<AdminRegister> getAdmin = adminregisterrepository.findAllByOrderByIdAsc();
            
            // Check if the list is empty
            if (getAdmin.isEmpty()) {
                return new ResponseEntity(HttpStatus.NO_CONTENT); // Return 204 No Content
            }
            // Return the list of users with 200 OK
            return new ResponseEntity<>(getAdmin, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and return 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<String> deleteAdminById(
	    @RequestHeader("Authorization") String token,
	    @PathVariable("id") Long id) {

	    try {
	        // Check if requester is ADMIN
	        String role = jwtUtil.getRoleFromToken(token);
	        if (!"ADMIN".equals(role)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                                 .body("{\"message\": \"Only admin can delete subadmin\"}");
	        }

	        // Find the admin
	        Optional<AdminRegister> adminOpt = adminregisterrepository.findById(id);
	        if (adminOpt.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                 .body("{\"message\": \"Admin not found\"}");
	        }

	        AdminRegister admin = adminOpt.get();

	        // Now delete the admin
	        adminregisterrepository.delete(admin);

	        return ResponseEntity.ok("{\"message\": \"Admin deleted and removed from departments successfully\"}");

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("{\"message\": \"An error occurred while deleting the admin.\"}");
	    }
	}
	
	@PostMapping("/invite")
    public ResponseEntity<String> sendInvitation(@RequestParam String email, @RequestParam String role,@RequestHeader("Authorization") String token) {
		try {
			
			String roles =  jwtUtil.getRoleFromToken(token);
			
			 System.out.println("role"+ role);

	            if (!"ADMIN".equals(roles)) {
	                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"Only admin can add agent and admin\"}");
	            }
			// Check if email already exists
	        if (adminregisterrepository.findByEmail(email).isPresent()) {
	            return ResponseEntity.badRequest().body("Email is already registered.");
	        }

	     // Retrieve the role based on the passed role value
	        Role userRole = rolerepository.findByRole(role.toUpperCase())
	        	    .orElseThrow(() -> new RuntimeException("Role '" + role.toUpperCase() + "' not found"));;
	        
	        AdminRegister newUser = new AdminRegister();
	        newUser.setEmail(email);
	        newUser.setRole(userRole);
	        newUser.setStatus(false);
	        newUser.setCode(UUID.randomUUID().toString());
        
        String link = "http://localhost:3000/#/register?token=" + newUser.getCode();
        String subject = "You have been invited to join";
        String body = "Hi,\n\nYou have been invited to join. Click the link below to register:\n\n" + 
                      link + "\n\n-chatbot  Team";
        
        emailservice.sendEmail(email, subject, body); // This calls the email service 
        
        adminregisterrepository.save(newUser);
        
        return ResponseEntity.ok("Invitation sent successfully.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    }
}
	
	
	@GetMapping("/register-token/{token}")
	public ResponseEntity<?> getDetailsFromToken(@PathVariable String token) {
	    Optional<AdminRegister> invitation = adminregisterrepository.findByCode(token);
	    if (invitation.isPresent()) {
	        Map<String, String> response = new HashMap<>();
	        response.put("email", invitation.get().getEmail());
	        response.put("role", invitation.get().getRole().getRole());
	        return ResponseEntity.ok(response);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or expired token.");
	    }
	}

	
	@GetMapping("/getadminnames")
	public ResponseEntity<List<AdminUserDto>> getAllUsernamesAndIds() {
	    try {
	        List<AdminUserDto> users = adminregisterrepository.findAllUserIdAndUsernameAndDepartment();
	        
	        if (users.isEmpty()) {
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        }
	        
	        return new ResponseEntity<>(users, HttpStatus.OK);
	        
	    } catch (Exception e) {
	        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	@GetMapping("/getAdminbyid/{id}")
	 public ResponseEntity<AdminRegister> getUserById(@PathVariable Long id) {
	        Optional<AdminRegister> userOptional = adminregisterrepository.findById(id);
	        if (userOptional.isPresent()) {
	        	AdminRegister user = userOptional.get();
	            return ResponseEntity.ok(user);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	 }
	
	@PatchMapping("/updateAdmin/{id}")
	public ResponseEntity<?> updateAdmin(@RequestHeader("Authorization") String token,
	                                     @PathVariable Long id,
	                                     @RequestParam("email") String email,
	                                     @RequestParam("role") String role) {
	    try {
	        String roles = jwtUtil.getRoleFromToken(token);
	        if (!"ADMIN".equals(roles)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"Only admin can update\"}");
	        }
	        Optional<AdminRegister> optionalAdmin = adminregisterrepository.findById(id);
	        if (optionalAdmin.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("{\"message\": \"admin or agent not found\"}");
	        }

	        // 🔍 Check if email is already used by another user
	        Optional<AdminRegister> emailExists = adminregisterrepository.findByEmail(email);
	       // Option 1: Use != for primitive comparison
	        if (emailExists.isPresent() && emailExists.get().getId() != id) {
	            return ResponseEntity.status(HttpStatus.CONFLICT)
	                    .body("{\"message\": \"Email is already registered\"}");
	        }

	        Role userRole = rolerepository.findByRole(role.toUpperCase())
	                .orElseThrow(() -> new RuntimeException("Role '" + role.toUpperCase() + "' not found"));

	        AdminRegister adminregister = optionalAdmin.get();
	        if (adminregister.isStatus()) {
	            return ResponseEntity.ok().body("{\"message\": \"No need to update\"}");
	        } else {
	            adminregister.setEmail(email);
	            adminregister.setRole(userRole);
	            adminregister.setStatus(false);
	            adminregister.setCode(UUID.randomUUID().toString());

	            String link = "http://localhost:3000/#/register?token=" + adminregister.getCode();
	            String subject = "You have been invited to join";
	            String body = "Hi,\n\nYou have been invited to join. Click the link below to register:\n\n" +
	                          link + "\n\n-chatbot Team";

	            emailservice.sendEmail(email, subject, body);
	            adminregisterrepository.save(adminregister);
	        }

	        return ResponseEntity.ok().body("{\"message\": \"Department updated successfully\"}");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("{\"message\": \"Error while updating department\"}");
	    }
	}
	
	@GetMapping("/check-admin-exists")
	public ResponseEntity<Boolean> checkIfAdminExists() {
	    boolean adminExists = adminregisterrepository.existsAdminByRole();
	    return ResponseEntity.ok(adminExists);
	}


}
