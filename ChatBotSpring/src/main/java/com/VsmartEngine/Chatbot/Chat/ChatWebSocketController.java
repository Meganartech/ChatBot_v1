package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin()
@RestController
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository repository;
    
    @Autowired
    private ChatSessionRepository chatsessionrepository;

    // important
    @MessageMapping("/send")
    public void sendMessage(@Payload MessageDTO message) {
        ChatMessage saved = new ChatMessage();
        
        saved.setSessionId(message.getSessionId());
        saved.setSender(message.getSender());
        saved.setReceiver(message.getReceiver());
        saved.setContent(message.getContent());
        saved.setRole(message.getRole());
        saved.setTimestamp(LocalDateTime.now());
        repository.save(saved);

        messagingTemplate.convertAndSend("/topic/messages/" + message.getSessionId(), message);
    }

    @GetMapping("/api/history/{sessionId}")
    public List<ChatMessage> getHistory(@PathVariable String sessionId) {
        return repository.findBySessionIdOrderByTimestampAsc(sessionId);
    }
}
