package com.example.LiveChat.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeminiService {
    private static final String API_KEY = "AIzaSyD8OAfKmDmJL11ltjhr6m0frXj_Yzrt7vU"; // 🔹 Replace with your actual API Key
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyD8OAfKmDmJL11ltjhr6m0frXj_Yzrt7vU";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String getAIResponse(String userMessage) {
        try {
            // ✅ Step 1: Prepare JSON Request Body
            String requestBody = objectMapper.writeValueAsString(Map.of(
                "contents", new Object[] {
                    Map.of("parts", new Object[] { Map.of("text", userMessage) })
                }
            ));

            // ✅ Step 2: Set HTTP Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ Step 3: Create HTTP Entity
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // ✅ Step 4: Send API Request
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                GEMINI_API_URL, HttpMethod.POST, requestEntity, String.class
            );

            // ✅ Step 5: Parse API Response
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
                JsonNode textNode = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text");
                return textNode.asText();
            } else {
                return "AI Response Error: " + responseEntity.getStatusCode();
            }

        } catch (Exception e) {
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
