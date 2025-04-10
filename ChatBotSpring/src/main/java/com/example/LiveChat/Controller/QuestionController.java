package com.example.LiveChat.Controller;

import com.example.LiveChat.DTO.QuestionRequest;
import com.example.LiveChat.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /** 🔹 Add a New Question (Linked to a Property) */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addQuestion(
            @RequestHeader("Authorization") String token, 
            @RequestBody String questionJson) {
        return ResponseEntity.ok(questionService.addQuestions(token.replace("Bearer ", ""), questionJson));
    }

    /** 🔹 Update an Existing Question */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateQuestion(
            @RequestHeader("Authorization") String token, 
            @PathVariable Long id, 
            @RequestBody String questionJson) {
        return ResponseEntity.ok(questionService.updateQuestion(token.replace("Bearer ", ""), id, questionJson));
    }

    /** 🔹 Remove a Question */
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Map<String, Object>> removeQuestion(
            @RequestHeader("Authorization") String token, 
            @PathVariable Long id) {
        return ResponseEntity.ok(questionService.removeQuestion(token.replace("Bearer ", ""), id));
    }
    
    /** 🔹 Get All Questions for a Specific Property */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Map<String, Object>> getQuestionsByProperty(
            @RequestHeader("Authorization") String token, 
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(questionService.getQuestionsByProperty(token.replace("Bearer ", ""), propertyId));
    }
    
    @PostMapping("/answer")
    public ResponseEntity<Map<String, Object>> getAnswerForQuestion(
            @RequestHeader("Authorization") String token,
            @RequestBody QuestionRequest request) {
        Map<String, Object> response = questionService.getAnswerForQuestion(
                token.replace("Bearer ", ""), request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchQuestionsByKeyword(
            @RequestHeader("Authorization") String token, 
            @RequestParam String keyword, 
            @RequestParam Long propertyId) {
        return ResponseEntity.ok(questionService.searchQuestionsByKeyword(token.replace("Bearer ", ""), keyword, propertyId));
    }
}
