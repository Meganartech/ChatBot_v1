//package com.VsmartEngine.Chatbot;
//
//import java.security.Principal;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.VsmartEngine.Chatbot.Admin.AdminRegisterController;
//
//@CrossOrigin()
//@RestController
//@RequestMapping("/chatbot")
//public class FrontController {
//	
//	@Autowired
//	private AdminRegisterController adminregistercontroller;
//	
//	@PostMapping("/register")
//    public ResponseEntity<?> register(@RequestParam("username") String username,
//                                           @RequestParam("email") String email,
//                                           @RequestParam("password") String password,
//                                           @RequestParam(value="role",required=false) String role,
//                                           Principal principal) {
//
//		return adminregistercontroller.
//	}
//	
//	@PostMapping("/login")
//	public ResponseEntity<?> AdminLogin(@RequestBody Map<String, String> loginRequest){
//		return adminregistercontroller.AdminLogin(loginRequest);
//	}
//
//}
