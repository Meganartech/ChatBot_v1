package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.VsmartEngine.Chatbot.Admin.AdminRegisterRepository;
import com.VsmartEngine.Chatbot.UserInfo.UserInfoRepository;

@CrossOrigin()
@Controller
public class ChatSessionController {
	
	@Autowired
	private ChatSessionRepository chatsessionrepository;
	
	@Autowired
	private UserInfoRepository userinforepository;
	
	@Autowired
	private AdminRegisterRepository adminregisterrepository;
	
	@Autowired
	private ChatMessageRepository chatmessagerepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	// Create or reuse existing session
    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestParam String sender,
                                          @RequestParam String receiver) {
        try {

            ChatSession session = new ChatSession();
                session.setCreatedTime(LocalDateTime.now());
                session.setReceiver(receiver);
                session.setSender(sender);
                session.setSessionId(UUID.randomUUID().toString());
                session.setStatus(false);
                chatsessionrepository.save(session);

            // Notify the receiver (admin) in real-time about the new session
            MessageDisplay display = new MessageDisplay();
            display.setSessionId(session.getSessionId());
            display.setStatus(session.isStatus());

            userinforepository.findByEmail(session.getSender()).ifPresent(user -> {
                display.setUserid(user.getId());
                display.setUsername(user.getUsername());
                display.setUseremail(user.getEmail());
            });

            adminregisterrepository.findByEmail(session.getReceiver()).ifPresent(admin -> {
                display.setAdminid(admin.getId());
                display.setReceivername(admin.getUsername());
                display.setAdminemail(admin.getEmail());
            });

            display.setUnreadCount(0L);
            // Provide a default preview message and timestamp for UI lists
            display.setMessage("New chat");
            display.setTimestamp(LocalDateTime.now());
            messagingTemplate.convertAndSend("/topic/sessions/" + receiver, display);
            // Also broadcast on a general topic so clients can filter locally if needed
            messagingTemplate.convertAndSend("/topic/sessions", display);

            return ResponseEntity.ok(Collections.singletonMap("sessionId", session.getSessionId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating session: " + e.getMessage());
        }
    }
        
    @GetMapping("/sessions")
    public ResponseEntity<List<MessageDisplay>> getSessionsByReceiver(@RequestParam String receiverEmail) {
        List<ChatSession> sessions = chatsessionrepository.findByReceiver(receiverEmail);
        List<MessageDisplay> displayList = new ArrayList<>();
        // Preload unread counts in a single query to avoid per-session COUNT queries
        List<Object[]> unreadRows = chatmessagerepository.findUnreadCountsByReceiver(receiverEmail);
        java.util.Map<String, Long> unreadBySession = new java.util.HashMap<>();
        for (Object[] row : unreadRows) {
            String sid = String.valueOf(row[0]);
            Long cnt = (row[1] instanceof Number) ? ((Number) row[1]).longValue() : 0L;
            unreadBySession.put(sid, cnt);
        }

        for (ChatSession session : sessions) {

            MessageDisplay display = new MessageDisplay();
            display.setSessionId(session.getSessionId());
            display.setStatus(session.isStatus());

            userinforepository.findByEmail(session.getSender()).ifPresent(user -> {
                display.setUserid(user.getId());
                display.setUsername(user.getUsername());
                display.setUseremail(user.getEmail());
            });

            adminregisterrepository.findByEmail(session.getReceiver()).ifPresent(admin -> {
                display.setAdminid(admin.getId());
                display.setReceivername(admin.getUsername());
                display.setAdminemail(admin.getEmail());
            });

            ChatMessage latestMessage = chatmessagerepository.findTopBySessionIdOrderByTimestampDesc(session.getSessionId());
            if (latestMessage != null) {
                display.setMessage(latestMessage.getContent());
                display.setTimestamp(latestMessage.getTimestamp());
            }

            // Set unread from precomputed map (defaults to 0)
            display.setUnreadCount(unreadBySession.getOrDefault(session.getSessionId(), 0L));

            displayList.add(display);
        }

        return ResponseEntity.ok(displayList);
    }
    
    @GetMapping("/sessionlist")
    public ResponseEntity<List<SessionDisplayDTO>> getSessionList(@RequestParam String receiverEmail) {
        List<ChatSession> sessions = chatsessionrepository.findByReceiver(receiverEmail);
        List<SessionDisplayDTO> displayList = new ArrayList<>();

        for (ChatSession session : sessions) {
            if (!session.getReceiver().equalsIgnoreCase(receiverEmail)) {
                continue;
            }

            SessionDisplayDTO sessionDisplay = new SessionDisplayDTO();
            sessionDisplay.setUseremail(session.getSender());

            userinforepository.findByEmail(session.getSender()).ifPresent(user -> {
                sessionDisplay.setUsername(user.getUsername());
            });

            // 🔹 Calculate total messages for this session
            long totalMessages = chatmessagerepository.countBySessionId(session.getSessionId());
            sessionDisplay.setTotalMessage(totalMessages);

            // Optionally add time or agent status if needed
            
            sessionDisplay.setTime(session.getCreatedTime().toString());
            sessionDisplay.setAgents(session.getReceiver());


            displayList.add(sessionDisplay);
        }

        return ResponseEntity.ok(displayList);
    }
    
    
    @PostMapping("/setStatusForSessionID")
    public ResponseEntity<?> setStatus(@RequestParam("sessionId") String sessionId,
                                       @RequestParam("Status") boolean Status) {
        try {
        	System.out.println("Received sessionId: " + sessionId); 
            Optional<ChatSession> chatsession = chatsessionrepository.findBySessionId(sessionId);
            if (chatsession.isPresent()) {
                ChatSession session = chatsession.get();
                session.setStatus(Status); // ✅ pass boolean, not String
                chatsessionrepository.save(session);
                return ResponseEntity.ok("Status updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session ID not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }

    // Mark messages as read by admin for a session (like opening the chat)
    @PutMapping("/sessions/{sessionId}/markRead")
    public ResponseEntity<?> markSessionRead(@PathVariable String sessionId) {
        Optional<ChatSession> chatSession = chatsessionrepository.findBySessionId(sessionId);
        if (chatSession.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
        }
        ChatSession session = chatSession.get();
        session.setLastAdminReadAt(LocalDateTime.now());
        chatsessionrepository.save(session);
        return ResponseEntity.ok().build();
    }

    
    


}
