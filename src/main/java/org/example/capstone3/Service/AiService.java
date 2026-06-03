package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${anthropic.api.key}")
    private String anthropicApiKey;


    private String buildPrompt (){
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert  project evaluator.\n\n");
        sb.append("Project Details:\n");


        sb.append("Please evaluate each proposal and:\n");
        sb.append("1. Give each proposal a score out of 10\n");
        sb.append("2. Explain why in 2-3 sentences\n");
        sb.append("3. Recommend the BEST proposal with clear reasoning\n");
        sb.append("4. Format your response in a clear, structured way\n");

        return sb.toString() ;
    }
    public  String callClaudeApi(String prompt){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", anthropicApiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", "claude-sonnet-4-5");
        requestBody.put("max_tokens", 1000);
        requestBody.put("system", "Respond ONLY with raw JSON. No markdown, no backticks, no explanation.");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.anthropic.com/v1/messages",
                    request,
                    Map.class
            );


            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("content")) {
                List<Map<String, Object>> content = (List<Map<String, Object>>) body.get("content");
                if (!content.isEmpty()) {
                    String raw = (String) content.get(0).get("text"); // ← fixed, no more 'root'
                    return raw.replaceAll("(?s)```json\\s*|```", "").trim(); // ← clean and return
                }
            }
            throw new ApiException("Failed to get response from AI");

        } catch (Exception e) {
            throw new ApiException("AI evaluation failed: " + e.getMessage());
        }
    }

}
