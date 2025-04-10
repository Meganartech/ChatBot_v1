package com.example.LiveChat.Controller;

import com.example.LiveChat.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // ✅ Send Message API (Minimal Controller Logic)
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody String messageJson) {
        
        return ResponseEntity.ok(chatService.sendMessage(token.replace("Bearer ", ""), messageJson));
    }
    
    // ✅ Get Messages Between Users
    @GetMapping("/{propertyId}/{sender}/{receiver}")
    public ResponseEntity<Map<String, Object>> getMessagesBetweenUsers(
            @RequestHeader("Authorization") String token,
            @PathVariable Long propertyId,
            @PathVariable String sender,
            @PathVariable String receiver) {

        // Call service and return structured response
        Map<String, Object> response = chatService.getMessagesBetweenUsers(token.replace("Bearer ", ""), propertyId, sender, receiver);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{propertyId}/sent/{receiver}")
    public ResponseEntity<Map<String, Object>> getSentMessagesToUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long propertyId,
            @PathVariable String receiver) {

        // ✅ Call service and return structured response
        Map<String, Object> response = chatService.getSentMessagesToUser(token.replace("Bearer ", ""), propertyId, receiver);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{propertyId}/{sender}/received")
    public ResponseEntity<Map<String, Object>> getReceivedMessagesFromUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long propertyId,
            @PathVariable String sender) {

        // ✅ Call service and return structured response
        Map<String, Object> response = chatService.getReceivedMessagesFromUser(token.replace("Bearer ", ""), propertyId, sender);
        return ResponseEntity.ok(response);
    }

}
