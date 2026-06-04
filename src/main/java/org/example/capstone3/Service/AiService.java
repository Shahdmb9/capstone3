package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.Parent;
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


    public String buildPromptRiskPrediction (Habit habit){
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert habit analyst and risk evaluator.\n\n");
        sb.append("habit Details:\n");
        sb.append("- Name: ").append(habit.getTitle()).append("\n");
        sb.append("- Description: ").append(habit.getDescription()).append("\n");
        sb.append("- Frequency: ").append(habit.getFrequency()).append("\n");
        sb.append("- Points: ").append(habit.getPoints()).append("\n");


        sb.append("Please evaluate and Predict the risk  and:\n");
        sb.append("1. Predict the risk level of failing this habit (Low / Medium / High)\n");
        sb.append("2. Give a risk score out of 10 (10 = highest risk)\\n");
        sb.append("3. Provide a recommendation to reduce the risk\n");
        sb.append("4. Format your response in a clear, structured way \n");
        sb.append("Respond ONLY in this exact JSON format:\n");
        sb.append("{\n");
        sb.append("  \"riskLevel\": \"Low | Medium | High\",\n");
        sb.append("  \"riskScore\": 0-10,\n");
        sb.append("  \"explanation\": \"...\",\n");
        sb.append("  \"recommendation\": \"...\"\n");
        sb.append("}\n");
        return sb.toString() ;
    }
    public String buildPromptBestTime(Habit habit){
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert habit analyst and  evaluator.\n\n");
        sb.append("Habit Details:\n");
        sb.append("- Name: ").append(habit.getTitle()).append("\n");
        sb.append("- Description: ").append(habit.getDescription()).append("\n");
        sb.append("- Frequency: ").append(habit.getFrequency()).append("\n");
        sb.append("- Points: ").append(habit.getPoints()).append("\n");


        sb.append("Based on this habit, recommend the best time of day to perform it.\n");
        sb.append("Respond ONLY in this exact JSON format:\n");
        sb.append("{\n");
        sb.append("  \"bestTimeRange\": \"e.g. 7:00 AM - 9:00 AM\",\n");
        sb.append("  \"reason\": \"short explanation why this time works best\",\n");
        sb.append("  \"alternativeTime\": \"e.g. 6:00 PM - 7:00 PM\"\n");
        sb.append("}\n");

        return sb.toString() ;

    }
    public  String buildPromptFamilyScore(Parent  parent)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert family behavioral analyst and  evaluator.\n\n");
        sb.append("Family: ").append(parent.getFullName()).append("\n\n");
        sb.append("Children Performance Data:\n");
        for (Child child : parent.getChildren()) {
            long completedTasks = child.getTask().stream()
                    .flatMap(t -> t.getTaskApplications().stream())
                    .filter(app -> "APPROVED".equalsIgnoreCase(app.getApprovalStatus()))
                    .count();

            long totalTasks = child.getTask().stream()
                    .flatMap(t -> t.getTaskApplications().stream())
                    .count();

            long completedHabits = child.getHabit().stream()
                    .flatMap(h -> h.getLogs().stream())
                    .filter(log -> "COMPLETED".equalsIgnoreCase(log.getApprovalStatus()))
                    .count();

            long totalHabits = child.getHabit().stream()
                    .flatMap(h -> h.getLogs().stream())
                    .count();

            sb.append("- Child: ").append(child.getFullName()).append("\n");
            sb.append("  Tasks Completed: ").append(completedTasks).append(" / ").append(totalTasks).append("\n");
            sb.append("  Habits Completed: ").append(completedHabits).append(" / ").append(totalHabits).append("\n");
            sb.append("  Points: ").append(child.getPoints()).append("\n\n");
        }
        sb.append("Based on this data, respond ONLY in this exact JSON format:\n");
        sb.append("{\n");
        sb.append("  \"familyScore\": 0-100,\n");
        sb.append("  \"familyScoreLabel\": \"e.g. Excellent / Good / Needs Improvement\",\n");
        sb.append("  \"bestChild\": \"child name\",\n");
        sb.append("  \"bestChildReason\": \"why this child is the best\",\n");
        sb.append("  \"mostImprovedChild\": \"child name\",\n");
        sb.append("  \"mostImprovedReason\": \"why this child shows the most growth\",\n");
        sb.append("  \"familyAdvice\": \"one actionable tip for the whole family\"\n");
        sb.append("}\n");
        return sb.toString();

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

    public String generateWhatsAppMessage(String topic, String tone, String language) {
        String prompt = String.format(
                "Generate a WhatsApp message about: %s\n" +
                        "Tone: %s\n" +
                        "Language: %s\n\n" +
                        "Requirements:\n" +
                        "- Keep it concise and suitable for WhatsApp\n" +
                        "- Use appropriate emojis\n" +
                        "- Make it personal and engaging\n" +
                        "- Return ONLY the message text, nothing else",
                topic, tone, language
        );
        return callClaudeApi(prompt);
    }

}
