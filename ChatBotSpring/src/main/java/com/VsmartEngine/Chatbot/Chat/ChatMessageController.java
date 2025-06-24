package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.VsmartEngine.Chatbot.Admin.AdminRegister;
import com.VsmartEngine.Chatbot.Admin.AdminRegisterRepository;
import com.VsmartEngine.Chatbot.TokenGeneration.JwtUtil;
import com.VsmartEngine.Chatbot.UserInfo.UserInfo;
import com.VsmartEngine.Chatbot.UserInfo.UserInfoRepository;
import com.VsmartEngine.Chatbot.UserInfo.UserService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin()
@RequestMapping("/chatbot")
@RestController
public class ChatMessageController {
	
	@Autowired
	private ChatMessageRepository chatmessagerepository;
	
	@Autowired
	private ChatSessionRepository chatsessionrepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserInfoRepository userinforepository;
	
	@Autowired
	private AdminRegisterRepository adminregisterrepository;
	
	@Autowired
	private UserService userservice;
	
	@Autowired
	private ChatSessionService chatsessionservice;
	
//	private ChatSession getOrCreateSession(Long senderId, Long receiverId) {
//	    // Search for an existing session with sender-receiver or receiver-sender, and active (status = true)
//	    ChatSession existingSession = chatsessionrepository
//	        .findActiveSessionBetweenUsers(senderId, receiverId)
//	        .orElse(null);
//
//	    if (existingSession != null) {
//	        return existingSession;
//	    }
//
//	    // Otherwise, create a new session
//	    ChatSession newSession = new ChatSession();
//	    newSession.setSenderid(senderId);
//	    newSession.setReceiverid(receiverId);
//	    newSession.setStatus(true);
//	    newSession.setUserId(senderId);
//	    newSession.setAdminId(receiverId);
//	    return chatsessionrepository.save(newSession);
//	}
	
	private ChatSession getOrCreateSession(Long senderId, Long receiverId) {
	    // Look for any session (regardless of direction), active or inactive
	    Optional<ChatSession> optionalSession = chatsessionrepository
	        .findLatestSessionBetweenUsers(senderId, receiverId);

	    if (optionalSession.isPresent()) {
	        ChatSession existingSession = optionalSession.get();

	        // If session is still active, reuse it
	        if (existingSession.isStatus()) {
	            return existingSession;
	        }

	        // If session is inactive, mark it closed (optional: already false) and create a new one
	        existingSession.setStatus(false);
	        chatsessionrepository.save(existingSession); // Save status update before creating new
	    }

	    // Create a new session
	    ChatSession newSession = new ChatSession();
	    newSession.setSenderid(senderId);
	    newSession.setReceiverid(receiverId);
	    newSession.setStatus(true); // Active
	    newSession.setUserId(senderId);
	    newSession.setAdminId(receiverId);

	    return chatsessionrepository.save(newSession);
	}
	
	@PostMapping("/send")
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody MessageRequestDTO request) {

        ChatSession session = chatsessionservice.getOrCreateSession(request.getSender(), request.getReceiver());

        String role = jwtUtil.getRoleFromToken(token);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(request.getSender());
        chatMessage.setReceiver(request.getReceiver());
        chatMessage.setMessage(request.getMessage());
        chatMessage.setSessionId(session.getSessionid());
        chatMessage.setRole(role);

        ChatMessage saved = chatmessagerepository.save(chatMessage);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setId(saved.getId());
        response.setSender(saved.getSender());
        response.setReceiver(saved.getReceiver());
        response.setMessage(saved.getMessage());
        response.setRole(role);
        response.setTimestamp(saved.getTimestamp());

        return ResponseEntity.ok(response);
    }


//    // Send message
//    @PostMapping("/send")
//    public ResponseEntity<MessageResponseDTO> sendMessage(@RequestHeader("Authorization") String token,@RequestBody MessageRequestDTO request) {
//        ChatSession session = getOrCreateSession(request.getSender(), request.getReceiver());
//        
//        String role = jwtUtil.getRoleFromToken(token);
//
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setSender(request.getSender());
//        chatMessage.setReceiver(request.getReceiver());
//        chatMessage.setMessage(request.getMessage());
//        chatMessage.setSessionId(session.getSessionid());
//        chatMessage.setRole(role);
//
//        ChatMessage saved = chatmessagerepository.save(chatMessage);
//
//        MessageResponseDTO response = new MessageResponseDTO();
//        response.setId(saved.getId());
//        response.setSender(saved.getSender());
//        response.setReceiver(saved.getReceiver());
//        response.setMessage(saved.getMessage());
//        response.setRole(role);
//        response.setTimestamp(saved.getTimestamp());
//
//        return ResponseEntity.ok(response);
//    }

    // Get all messages for a session
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
    	
        return ResponseEntity.ok(chatmessagerepository.findBySessionIdOrderByTimestampAsc(sessionId));
    }
    
//    @GetMapping("/all")
//    public ResponseEntity<List<ChatSession>> getAllSessions() {
//        List<ChatSession> sessions = chatsessionrepository.findAll();
//        return ResponseEntity.ok(sessions);
//    }
    
    @GetMapping("/all")
    public ResponseEntity<List<MessageDTO>> getAllSessions() {
        List<ChatSession> sessions = chatsessionrepository.findAll();
        List<MessageDTO> result = sessions.stream().map(session -> {
            MessageDTO dto = new MessageDTO();
            dto.setSessionId(session.getSessionid());
            dto.setSenderid(session.getSenderid());
            dto.setReceiver(session.getReceiverid());
            
            Optional<UserInfo> user = userinforepository.findById(session.getSenderid());
            if(user.isPresent()) {
            	UserInfo info = user.get();
            	dto.setSenderName(info.getUsername());
            }
            
            Optional<AdminRegister> admin = adminregisterrepository.findById(session.getReceiverid());
            if(admin.isPresent()) {
            	AdminRegister info = admin.get();
            	dto.setReceiverName(info.getUsername());
            }

            // Get latest message
            ChatMessage lastMessage = chatmessagerepository
                    .findTopBySessionIdOrderByTimestampDesc(session.getSessionid());

            if (lastMessage != null) {
                dto.setMessage(lastMessage.getMessage());
                dto.setTimestamp(lastMessage.getTimestamp());
            }

            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/chatbot/get-session-id")
    public ResponseEntity<Map<String, Object>> getSessionId(
            @RequestParam Long sender,
            @RequestParam Long receiver) {

        ChatSession session = getOrCreateSession(sender, receiver);
        return ResponseEntity.ok(Map.of("sessionId", session.getSessionid()));
    }
    
    @GetMapping("/{sessionId}/sent/{receiver}")
    public ResponseEntity<Map<String, Object>> getSentMessagesToUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId,
            @PathVariable Long receiver) {

        Map<String, Object> response = new HashMap<>();
        Long senderId = null;

        try {
            // ✅ Extract sender email from JWT
            String senderEmail = jwtUtil.getEmailFromToken(token);

            // ✅ Check if sender is Admin or User
            Optional<AdminRegister> admin = adminregisterrepository.findByEmail(senderEmail);
            Optional<UserInfo> user = userinforepository.findByEmail(senderEmail);

            if (admin.isPresent()) {
                senderId = admin.get().getId();
            } else if (user.isPresent()) {
                senderId = user.get().getId();
            } else {
                response.put("status", "error");
                response.put("message", "Unauthorized sender");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // ✅ Fetch messages from sender to receiver in this session
            List<ChatMessage> sentMessages =
                chatmessagerepository.findChatBySessionIdAndSenderAndReceiver(sessionId, senderId, receiver);

            response.put("status", "success");
            response.put("message", "Messages retrieved successfully");
            response.put("chat", sentMessages);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to retrieve messages: " + e.getMessage());
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{sessionId}/{sender}/received")
    public ResponseEntity<Map<String, Object>> getReceivedMessagesFromUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sessionId,
            @PathVariable Long sender) {
    	
    	Map<String, Object> response = new HashMap<>();
    	Long receiverId = null;

        try {
            // ✅ Extract receiver email from JWT token
            String receiverEmail = jwtUtil.getEmailFromToken(token);
            
            // ✅ Check if sender is Admin or User
            Optional<AdminRegister> admin = adminregisterrepository.findByEmail(receiverEmail);
            Optional<UserInfo> user = userinforepository.findByEmail(receiverEmail);

            if (admin.isPresent()) {
            	receiverId = admin.get().getId();
            } else if (user.isPresent()) {
            	receiverId = user.get().getId();
            } else {
                response.put("status", "error");
                response.put("message", "Unauthorized sender");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // ✅ Fetch messages sent by the given sender to the authenticated user (receiver)
            List<ChatMessage> receivedMessages = chatmessagerepository.findChatBySessionIdAndSenderAndReceiver(sessionId, sender, receiverId);

            // ✅ Prepare success response
            response.put("status", "success");
            response.put("message", "Messages retrieved successfully");
            response.put("chat", receivedMessages);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to retrieve messages");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return ResponseEntity.ok(response);
    }
    
//    @GetMapping("/{propertyId}/{sender}/received")
//    public ResponseEntity<Map<String, Object>> getReceivedMessagesFromUser(
//            @RequestHeader("Authorization") String token,
//            @PathVariable Long propertyId,
//            @PathVariable String sender) {
//
//        // ✅ Call service and return structured response
//        Map<String, Object> response = chatService.getReceivedMessagesFromUser(token.replace("Bearer ", ""), propertyId, sender);
//        return ResponseEntity.ok(response);
//    }
    
    
//    @PostMapping("/ping")
//    public ResponseEntity<?> ping(HttpServletRequest request) {
//        String userEmail = extractUserEmailFromToken(request); // Implement based on JWT
//        userservice.updateLastSeen(userEmail, LocalDateTime.now());
//        return ResponseEntity.ok().build();
//    }
//
//    private String extractUserEmailFromToken(HttpServletRequest request) {
//        // Extract JWT from header
//        String token = request.getHeader("Authorization");
//
//        // Extract email from JWT using your JWT utility
//        return jwtUtil.getEmailFromToken(token); // You must implement this
//    }
    
    @PostMapping("/ping")
    public ResponseEntity<?> ping(HttpServletRequest request) {
    	System.out.println("request"+request);
        String userEmail = extractUserEmailFromToken(request);
        System.out.println("username"+userEmail);
        userservice.updateLastSeen(userEmail, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    private String extractUserEmailFromToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return jwtUtil.getEmailFromToken(token);
        } else {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
    	
    	 // Extract JWT from header
//      String token = request.getHeader("Authorization");
//      System.out.println("token"+token);
//
//      // Extract email from JWT using your JWT utility
//      return jwtUtil.getEmailFromToken(token); // You must implement this
//    }
    }


}
