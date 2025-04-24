package com.VsmartEngine.Chatbot.Admin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.VsmartEngine.Chatbot.MailConfiguration.EmailService;
import com.VsmartEngine.Chatbot.TokenGeneration.JwtUtil;

@CrossOrigin()
@RequestMapping("/chatbot")
@Controller
public class AdminRegisterController {
	
	@Autowired
	private AdminRegisterRepository adminregisterrepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private RoleRepository rolerepository;
	
	@Autowired
	private EmailService emailservice;
	
	
//	@PostMapping("/register")
//    public ResponseEntity<?> register(@RequestParam("username") String username,
//                                           @RequestParam("email") String email,
//                                           @RequestParam("password") String password,
//                                           @RequestParam(value="role",required=false) String role,
//                                           Principal principal) {
//        try {
//            long count = adminregisterrepository.count();
//
//            //  If users already exist, only ADMIN can register new users
//            if (count > 0) {
//                if (principal == null) {
//                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//                }
//
//                Optional<AdminRegister> requestingUserOpt = adminregisterrepository.findByEmail(principal.getName());
//                if (requestingUserOpt.isEmpty() || 
//                    !requestingUserOpt.get().getRole().equalsIgnoreCase("ADMIN")) {
//                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admin can add new users");
//                }
//            }
//
//            //  Check if the email is already registered
//            Optional<AdminRegister> existingUser = adminregisterrepository.findByEmail(email);
//            if (existingUser.isPresent()) {
//                return ResponseEntity.badRequest().body("Email is already registered.");
//            }
//
//            //  Encrypt password
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            String encodedPassword = passwordEncoder.encode(password);
//
//            //  Create new user object
//            AdminRegister newUser = new AdminRegister();
//            newUser.setUsername(username);
//            newUser.setEmail(email);
//            newUser.setPassword(encodedPassword);
//
//            //  First user becomes ADMIN automatically
//            if (count == 0) {
//                newUser.setRole("ADMIN");
//            } else {
//                // Optionally validate role input here (ADMIN or AGENT)
//                newUser.setRole(role.toUpperCase());
//            }
//
//            // 💾 Save to database
//            adminregisterrepository.save(newUser);
//            return ResponseEntity.ok("User registered successfully");
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }
// 
//	@PostMapping("/login")
//	public ResponseEntity<?> AdminLogin(@RequestBody Map<String, String> loginRequest){
//		try {
//	        String email = loginRequest.get("email");
//	        String password = loginRequest.get("password");
//	        Optional<AdminRegister> admin = adminregisterrepository.findByEmail(email);
//	        if (!admin.isPresent()) {
//	            // User with the provided email doesn't exist
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
//	        }
//	        AdminRegister adminregister = admin.get();
//	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//	        
//	        if (!passwordEncoder.matches(password, adminregister.getPassword())) {
//	            // Incorrect password
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Incorrect password\"}");
//	        }
//	        String role = adminregister.getRole();
//	        String jwtToken = jwtUtil.generateToken(email,role);
//	        Map<String, Object> responseBody = new HashMap<>();
//	        responseBody.put("token", jwtToken);
//	        responseBody.put("message", "Login successful");
//	        responseBody.put("name", adminregister.getUsername());
//	        responseBody.put("email", adminregister.getEmail());
//	        responseBody.put("userId", adminregister.getId());
//	        responseBody.put("role", adminregister.getRole());	        
//	        System.out.println("token" + jwtToken);
//	        
//	        return ResponseEntity.ok(responseBody);
//
//		} catch(Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//		}
//	}

	
//	@PostMapping("/invite")
//	public ResponseEntity<?> inviteUser(@RequestParam("email") String email,
//	                                    @RequestParam("Authorization") String token) {
//	    try {
//	        String role = jwtUtil.getRoleFromToken(token);
//	        if (!"ADMIN".equals(role)) {
//	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//	                    .body("{\"message\": \"Only admin can invite users.\"}");
//	        }
//
//	        Optional<AdminRegister> existingUser = adminregisterrepository.findByEmail(email);
//	        if (existingUser.isPresent()) {
//	            return ResponseEntity.badRequest().body("{\"message\": \"Email is already registered.\"}");
//	        }
//
//	        // Get admin's email from token
//	        String adminEmail = jwtUtil.getEmailFromToken(token);
//
//	        // Invitation link
//	        String inviteLink = "http://your-frontend-domain.com/register?email=" +
//	                URLEncoder.encode(email, StandardCharsets.UTF_8);
//
//	        // Send email content
//	        String subject = "You're Invited to Join Vsmart ChatBot";
//	        String htmlContent = "<p>You have been invited by <b>" + adminEmail + "</b> to register on Vsmart ChatBot.</p>" +
//	                "<p>Click the button below to create your account:</p>" +
//	                "<p><a href=\"" + inviteLink + "\" style=\"padding: 10px 20px; background-color: #4CAF50; color: white; " +
//	                "text-decoration: none; border-radius: 5px;\">Create Account</a></p>";
//
////	        emailService.sendHtmlEmail(adminEmail,email,subject, htmlContent);
//
//	        return ResponseEntity.ok("{\"message\": \"Invitation sent successfully.\"}");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body("{\"message\": \"Internal server error.\"}");
//	    }
//	}
	
	
	@PostMapping("/register")
	public ResponseEntity<?> register(
	        @RequestParam("username") String username,
	        @RequestParam("email") String email,
	        @RequestParam("password") String password,
	        @RequestParam(value = "role", required = false) String role,
	        Principal principal) {

	    try {
	        long count = adminregisterrepository.count();

	        // Check if email already exists
	        if (adminregisterrepository.findByEmail(email).isPresent()) {
	            return ResponseEntity.badRequest().body("Email is already registered.");
	        }

	        AdminRegister newUser = new AdminRegister();
	        newUser.setUsername(username);
	        newUser.setEmail(email);
	        newUser.setPassword(new BCryptPasswordEncoder().encode(password));

	        // First user becomes ADMIN with ACTIVE status
	        if (count == 0) {
	            Role adminRole = rolerepository.findByRole("ADMIN")
	                    .orElseThrow(() -> new RuntimeException("Role 'ADMIN' not found"));


	            newUser.setRole(adminRole);
	            newUser.setStatus(true);

	            adminregisterrepository.save(newUser);
	            return ResponseEntity.ok("First Admin registered successfully.");
	        }

	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the first user can self-register.");

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error: " + e.getMessage());
	    }
	}


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
	        responseBody.put("role", adminregister.getRole());	        
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
            List<AdminRegister> getAdmin = adminregisterrepository.findAll();
            
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


	@PostMapping("/invite")
	public ResponseEntity<?> sendInvitation(@RequestParam String email, @RequestParam String role) {
	    try {
	    	String registrationLink = "http://localhost:3000/#/register?email=" + 
                    URLEncoder.encode(email, StandardCharsets.UTF_8) + 
                    "&role=" + 
                    URLEncoder.encode(role, StandardCharsets.UTF_8);

	        String subject = "You have been invited to join";
	        String body = "Hi,\n\nYou have been invited to join. Click the link below to register:\n\n" + 
	                      registrationLink + "\n\n-chatbot to Team";

	        emailservice.sendEmail(email, subject, body); // This calls the email service

	        return ResponseEntity.ok("Invitation sent successfully.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
	    }
	}

}
