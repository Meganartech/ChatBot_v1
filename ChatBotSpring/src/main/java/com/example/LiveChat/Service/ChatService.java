package com.example.LiveChat.Service;

import com.example.LiveChat.Model.Chat;
import com.example.LiveChat.Model.ChatbotType;
import com.example.LiveChat.Model.Property;
import com.example.LiveChat.Model.Questions;
import com.example.LiveChat.Repository.ChatRepo;
import com.example.LiveChat.Repository.PropertyRepo;
import com.example.LiveChat.Repository.QuestionRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private PropertyRepo propertyRepo;
    
    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private GeminiService geminiService;
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper; 

    @Transactional
    public Map<String, Object> sendMessage(String token, String messageJson) {
        Map<String, Object> response = new HashMap<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            Long propertyId = jsonNode.get("propertyId").asLong();
            String message = jsonNode.get("message").asText().trim();
            String receiver = jsonNode.get("receiver").asText();
            String senderType = jsonNode.get("senderType").asText();

            // ✅ Validate token
            if (token == null || token.isBlank() || !jwtService.validateToken(token)) {
                return errorResponse("Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
            }

            String senderEmail = jwtService.extractEmail(token);
            if (senderEmail == null || senderEmail.isBlank()) {
                return errorResponse("Invalid token: Unable to extract user details", HttpStatus.UNAUTHORIZED);
            }

            Optional<Property> propertyOptional = propertyRepo.findById(propertyId);
            if (propertyOptional.isEmpty()) {
                return errorResponse("Property not found: " + propertyId, HttpStatus.NOT_FOUND);
            }

            Property property = propertyOptional.get();
            ChatbotType chatbotType = property.getChatbotType();

            if (chatbotType == ChatbotType.ONE_TO_ONE) {
                // ✅ Normal One-to-One Chat: Save user message
                Chat userChat = new Chat();
                userChat.setSender(senderEmail);
                userChat.setReceiver(receiver);
                userChat.setMessage(message);
                userChat.setPropertyId(propertyId);
                userChat.setTimestamp(LocalDateTime.now());
                chatRepo.save(userChat);
            } else if (chatbotType == ChatbotType.KNOWLEDGE_BASED) {
                // ✅ Step 1: Store the user's message
                Chat userChat = new Chat();
                userChat.setSender(senderEmail);
                userChat.setReceiver(receiver);
                userChat.setMessage(message);
                userChat.setPropertyId(propertyId);
                userChat.setTimestamp(LocalDateTime.now());
                chatRepo.save(userChat);

                // ✅ Step 2: Extract keywords from the user message
                String[] keywords = message.split("\\s+"); // Simple keyword extraction (split by spaces)

                // ✅ Step 3: Find matching questions for each keyword
                Set<Questions> matchingQuestions = new HashSet<>();
                for (String keyword : keywords) {
                    matchingQuestions.addAll(questionRepo.findByKeywordsContainingAndPropertyId(keyword, propertyId));
                }

                String botResponse;
                if (!matchingQuestions.isEmpty()) {
                    // ✅ Step 4: Construct a response including all questions
                    StringBuilder responseText = new StringBuilder("Here are some related questions");
                    for (Questions q : matchingQuestions) {
                        responseText.append(",").append(q.getQuestion());
                    }
                    botResponse = responseText.toString().trim(); // Remove trailing newline

                    response.put("questions", matchingQuestions.stream()
                            .map(q -> Map.of(
                                    "propertyId", propertyId,
                                    "questionId", q.getId(),
                                    "question", q.getQuestion()
                            ))
                            .toList());
                } else {
                    // ✅ No matching questions found
                    botResponse = "I'm sorry, I couldn't find an answer.";
                }

               // ✅ Step 5: Store the bot’s response including questions
                Chat botChat = new Chat();
                botChat.setSender(receiver); // Admin/Bot as sender
                botChat.setReceiver(senderEmail); // User as receiver
                botChat.setMessage(botResponse);
                botChat.setPropertyId(propertyId);
                botChat.setTimestamp(LocalDateTime.now());
                
                Chat savedChat = chatRepo.save(botChat);  // Save and retrieve the entity
                response.put("response", botResponse);
                return response;
            } else if (chatbotType == ChatbotType.AI_CHAT) { 
                // ✅ Step 1: Store the user's message
                Chat userChat = new Chat();
                userChat.setSender(senderEmail);
                userChat.setReceiver(receiver);
                userChat.setMessage(message);
                userChat.setPropertyId(propertyId);
                userChat.setTimestamp(LocalDateTime.now());
                chatRepo.save(userChat);

                // ✅ Step 2: Send message to AI Model (Google Gemini)
                String aiResponse = geminiService.getAIResponse(message); 

                // ✅ Step 3: Handle AI response error case
                if (aiResponse == null || aiResponse.isBlank()) {
                    aiResponse = "I'm sorry, I couldn't generate a response at the moment.";
                }

                // ✅ Step 4: Store the AI’s response
                Chat aiChat = new Chat();
                aiChat.setSender(receiver); // AI/Bot as sender
                aiChat.setReceiver(senderEmail); // User as receiver
                aiChat.setMessage(aiResponse);
                aiChat.setPropertyId(propertyId);
                aiChat.setTimestamp(LocalDateTime.now());
                
                chatRepo.save(aiChat);

                // ✅ Step 5: Return response
                response.put("response", aiResponse);
                return response;
            } 


            response.put("status", "success");
            response.put("message", "Message Sent Successfully");

        } catch (JsonProcessingException e) {
            return errorResponse("Invalid JSON format: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return response;
    }
    
    public Map<String, Object> getMessagesBetweenUsers(String token, Long propertyId, String sender, String receiver) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate token
            if (token == null || token.isBlank() || !jwtService.validateToken(token)) {
                response.put("status", "error");
                response.put("message", "Invalid or expired JWT token");
                response.put("code", HttpStatus.UNAUTHORIZED.value());
                return response;
            }

            // Fetch messages from the repository
            List<Chat> chats = chatRepo.findChatByPropertyIdAndSenderAndReceiver(propertyId, sender, receiver);

            // ✅ Prepare success response
            response.put("status", "success");
            response.put("message", "Messages retrieved successfully");
            response.put("chatCount", chats.size());
            response.put("chats", chats);

        } catch (Exception e) {
            // Handle unexpected errors
            response.put("status", "error");
            response.put("message", "An error occurred while retrieving messages");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    public Map<String, Object> getSentMessagesToUser(String token, Long propertyId, String receiver) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ Extract sender email from JWT token
            String senderEmail = jwtService.extractEmail(token);

            // ✅ Fetch messages sent by the user to a specific receiver in a property
            List<Chat> sentMessages = chatRepo.findChatByPropertyIdAndSenderAndReceiver(propertyId, senderEmail, receiver);

            // ✅ Prepare success response
            response.put("status", "success");
            response.put("message", "Messages retrieved successfully");
            response.put("chat", sentMessages);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to retrieve messages");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }
    
    public Map<String, Object> getReceivedMessagesFromUser(String token, Long propertyId, String sender) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ Extract receiver email from JWT token
            String receiverEmail = jwtService.extractEmail(token);

            // ✅ Fetch messages sent by the given sender to the authenticated user (receiver)
            List<Chat> receivedMessages = chatRepo.findChatByPropertyIdAndSenderAndReceiver(propertyId, sender, receiverEmail);

            // ✅ Prepare success response
            response.put("status", "success");
            response.put("message", "Messages retrieved successfully");
            response.put("chat", receivedMessages);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to retrieve messages");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }  
    
    private Map<String, Object> errorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        response.put("code", status.value());
        return response;
    }

}
