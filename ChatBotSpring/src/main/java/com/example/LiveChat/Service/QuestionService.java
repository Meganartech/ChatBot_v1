package com.example.LiveChat.Service;

import com.example.LiveChat.DTO.QuestionRequest;
import com.example.LiveChat.Model.Chat;
import com.example.LiveChat.Model.Property;
import com.example.LiveChat.Model.Questions;
import com.example.LiveChat.Repository.ChatRepo;
import com.example.LiveChat.Repository.PropertyRepo;
import com.example.LiveChat.Repository.QuestionRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuestionService {

    @Autowired private QuestionRepo questionRepo;
    @Autowired private PropertyRepo propertyRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;
    @Autowired private ChatRepo chatRepo;

    /** 🔹 Add Multiple Questions */
    public Map<String, Object> addQuestions(String token, String questionsJson) {
        return processQuestions(token, null, questionsJson, "add");
    }

    /** 🔹 Update an Existing Question */
    public Map<String, Object> updateQuestion(String token, Long id, String questionJson) {
        return processQuestions(token, id, questionJson, "update");
    }

    /** 🔹 Remove a Question */
    public Map<String, Object> removeQuestion(String token, Long id) {
        if (!jwtService.validateToken(token)) return errorResponse("Invalid or expired token");

        String email = jwtService.extractEmail(token);
        if (email == null) return errorResponse("Unauthorized");

        Optional<Questions> optionalQuestion = questionRepo.findById(id);
        if (optionalQuestion.isEmpty()) return errorResponse("Question not found");

        Questions question = optionalQuestion.get();
        if (!question.getCreatedBy().equals(email)) return errorResponse("Unauthorized!");

        questionRepo.delete(question);
        return successResponse("Question removed successfully");
    }

    /** 🔹 Get All Questions for a Property */
    public Map<String, Object> getQuestionsByProperty(String token, Long propertyId) {
        if (!jwtService.validateToken(token)) return errorResponse("Invalid or expired token");

        Optional<Property> propertyOptional = propertyRepo.findById(propertyId);
        if (propertyOptional.isEmpty()) return errorResponse("Property not found");

        List<Questions> questions = questionRepo.findByProperty(propertyOptional.get());
        return successResponse("Questions fetched successfully", questions);
    }

    /** 🔹 Add or Update Questions */
    private Map<String, Object> processQuestions(String token, Long id, String questionJson, String action) {
        if (!jwtService.validateToken(token)) return errorResponse("Invalid or expired token");

        try {
            JsonNode rootNode = objectMapper.readTree(questionJson);
            String email = jwtService.extractEmail(token);
            if (email == null) return errorResponse("Unauthorized");

            Long propertyId = rootNode.get("propertyId").asLong();
            Optional<Property> propertyOptional = propertyRepo.findById(propertyId);
            if (propertyOptional.isEmpty()) return errorResponse("Invalid property ID");

            Property property = propertyOptional.get();

            // Handle multiple questions for 'add' action
            if ("add".equals(action) && rootNode.has("questions") && rootNode.get("questions").isArray()) {
                List<Questions> questionsList = new ArrayList<>();

                for (JsonNode jsonNode : rootNode.get("questions")) {
                    if (!jsonNode.has("question") || !jsonNode.has("answer") || !jsonNode.has("keywords")) {
                        return errorResponse("Each question must contain 'question', 'answer', and 'keywords'");
                    }

                    Questions question = new Questions();
                    question.setQuestion(jsonNode.get("question").asText());
                    question.setAnswer(jsonNode.get("answer").asText());
                    question.setKeywords(jsonNode.get("keywords").asText());
                    question.setCreatedBy(email);
                    question.setProperty(property);

                    questionsList.add(question);
                }

                questionRepo.saveAll(questionsList);
                return successResponse("Questions added successfully", questionsList);
            }

            // Handle single question update
            if ("update".equals(action)) {
                if (id == null) return errorResponse("Question ID is required for update");

                Questions question = questionRepo.findById(id)
                        .filter(q -> q.getCreatedBy().equals(email))
                        .orElse(null);
                if (question == null) return errorResponse("Question not found or unauthorized update");

                if (!rootNode.has("question") || !rootNode.has("answer") || !rootNode.has("keywords")) {
                    return errorResponse("Invalid JSON format: Missing 'question', 'answer', or 'keywords'");
                }

                question.setQuestion(rootNode.get("question").asText());
                question.setAnswer(rootNode.get("answer").asText());
                question.setKeywords(rootNode.get("keywords").asText());
                question.setProperty(property);

                questionRepo.save(question);
                return successResponse("Question updated successfully", question);
            }

            return errorResponse("Invalid action. Use 'add' or 'update'");

        } catch (Exception e) {
            return errorResponse("Error processing question: " + e.getMessage());
        }
    }
    
    public Map<String, Object> searchQuestionsByKeyword(String token, String keyword, Long propertyId) {
        // Validate JWT token
        if (!jwtService.validateToken(token)) {
            return errorResponse("Invalid or expired token");
        }

        // Fetch questions
        List<Questions> questions = questionRepo.findByKeywordsContainingAndPropertyId(keyword, propertyId);
        
        // Return response
        return successResponse("Questions found successfully", questions);
    }
    
    public Map<String, Object> getAnswerForQuestion(String token, QuestionRequest request) {
        Map<String, Object> response = new HashMap<>();

        Long propertyId = request.getPropertyId();
        String questionText = request.getQuestion();

        // ✅ Validate Token
        if (token == null || token.isBlank() || !jwtService.validateToken(token)) {
            return errorResponse("Invalid or expired JWT token");
        }

        String userEmail = jwtService.extractEmail(token);
        if (userEmail == null || userEmail.isBlank()) {
            return errorResponse("Invalid token: Unable to extract user details");
        }

        // ✅ Check if Property Exists
        Optional<Property> propertyOptional = propertyRepo.findById(propertyId);
        if (propertyOptional.isEmpty()) {
            return errorResponse("Property not found: " + propertyId);
        }

        // ✅ Validate Question Text
        if (questionText == null || questionText.isBlank()) {
            return errorResponse("Question text cannot be empty");
        }

        // ✅ Fetch the Question within the Property using Question Text (Case-Insensitive)
        Optional<Questions> questionOptional = questionRepo.findByPropertyIdAndQuestionIgnoreCase(propertyId, questionText);
        if (questionOptional.isEmpty()) {
            return errorResponse("Question not found in this property");
        }

        // ✅ Get Question and Answer
        Questions question = questionOptional.get();
        String answerText = question.getAnswer();
        
        // ✅ Get the creator of the question
        String questionCreator = question.getCreatedBy();
        if (questionCreator == null || questionCreator.isBlank()) {
            return errorResponse("Question creator not found");
        }

        // ✅ Save User's Question as a Chat Message
        chatRepo.save(new Chat(userEmail, questionCreator, questionText, propertyId, LocalDateTime.now()));

        // ✅ Save Automatic Answer as a Chat Message
        chatRepo.save(new Chat(questionCreator, userEmail, answerText, propertyId, LocalDateTime.now()));

        // ✅ Prepare Response
        return successResponse("Message sent successfully", Map.of("question", questionText, "answer", answerText));
    }

    /** 🔹 Success Response Helper */
    private Map<String, Object> successResponse(String message) {
        return Map.of("status", "success", "message", message);
    }


    /** 🔹 Success Response with Data */
    private Map<String, Object> successResponse(String message, Object data) {
        return Map.of("status", "success", "message", message, "data", data);
    }

    /** 🔹 Error Response Helper */
    private Map<String, Object> errorResponse(String message) {
        return Map.of("status", "error", "message", message);
    }
}
