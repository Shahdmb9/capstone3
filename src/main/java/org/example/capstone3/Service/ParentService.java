package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.ParentDTOIn;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.HabitLog;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Repository.ChildRepository;
import org.example.capstone3.Repository.HabitLogRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final ModelMapper modelMapper;
    private final CreatePdfService createPdfService;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    private final AiService aiService;
    private final ChildRepository childRepository;
    private final HabitLogRepository habitLogRepository;

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public void add(ParentDTOIn parentIn) {
        Parent parent = modelMapper.map(parentIn, Parent.class);
        parent.setCreatedAt(java.time.LocalDateTime.now());
        parentRepository.save(parent);
    }

    public void delete(Integer id) {
        Parent parent = getParentById(id);
        parentRepository.delete(parent);
    }

    public void update(Integer id, ParentDTOIn parentin) {
        Parent parent = modelMapper.map(parentin, Parent.class);
        Parent oldParent = getParentById(id);
        oldParent.setEmail(parent.getEmail());
        oldParent.setFullName(parent.getFullName());
        oldParent.setPassword(parent.getPassword());
        parentRepository.save(oldParent);
    }

    public Parent getParentById(Integer id) {
        Parent parent = parentRepository.findParentById(id);
        if (parent == null) throw new ApiException("Parent not found");
        return parent;
    }
    public void deductChildPoint(Integer id, Integer childId, Integer points) {
        Parent parent = getParentById(id);
        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");
        if (!parent.getChildren().contains(child)) throw new ApiException("This is not your child");

        int updatePoints = child.getPoints() - points;
        if (updatePoints < 0) child.setPoints(0);
        else child.setPoints(updatePoints);

        childRepository.save(child);
    }

    public void ChildrenPerformanceReport(Integer id, String period) {
        Parent parent = parentRepository.findParentById(id);
        if (parent == null) throw new ApiException("Parent not found");
        byte[] reportPDF = createPdfService.generatePerformanceReportPdf(parent, period);
        emailService.sendReportByEmail(parent.getEmail(), reportPDF, parent.getFullName());
        whatsAppService.sendReportByWhatsApp(parent.getPhoneNumber(), parent.getFullName());
    }

    public byte[] childrenPerformanceReport(Integer id, String period) {
        Parent parent = parentRepository.findParentById(id);
        if (parent == null) throw new ApiException("Parent not found");
        return createPdfService.generatePerformanceReportPdf(parent, period);
    }

    public List<HabitLog> getPendingHabit(Integer parentId) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");

        List<HabitLog> pendingLogs = new ArrayList<>();
        for (Habit habit : parent.getHabit()) {
            if (habit.getChild() != null) {
                List<HabitLog> logs = habitLogRepository.findByHabitAndApprovalStatus(habit, "PENDING");
                pendingLogs.addAll(logs);
            }
        }
        if (pendingLogs.isEmpty()) throw new ApiException("No pending habit logs found for approval");
        return pendingLogs;
    }
    public String getChildBehaviorAnalysis(Integer parentId, Integer childId) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");

        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");
        if (!parent.getChildren().contains(child)) throw new ApiException("This is not your child");

        StringBuilder childMetrics = new StringBuilder();
        childMetrics.append("Child Name: ").append(child.getFullName()).append("\n");
        childMetrics.append("Age: ").append(child.getAge()).append("\n");
        childMetrics.append("Total Earned Points: ").append(child.getPoints()).append("\n");
        childMetrics.append("\n[Active Habits]:\n");
        for (Habit h : child.getHabit()) {
            childMetrics.append("- ").append(h.getTitle()).append(" (").append(h.getFrequency()).append(")\n");
        }

        childMetrics.append("\n[Past Commitment History & Logs]:\n");
        int completedCount = 0;
        int rejectedCount = 0;
        for (Habit habit : child.getHabit()) {
            if (habit.getLogs() != null) {
                for (HabitLog log : habit.getLogs()) {
                    childMetrics.append("- Habit: \"").append(habit.getTitle())
                            .append("\" | Date: ").append(log.getLoggedDate())
                            .append(" | Status: ").append(log.getApprovalStatus()).append("\n");
                    if ("COMPLETED".equalsIgnoreCase(log.getApprovalStatus())) completedCount++;
                    if ("REJECTED".equalsIgnoreCase(log.getApprovalStatus())) rejectedCount++;
                }
            }
        }
        childMetrics.append("\nSummary Counters -> Total Completed: ").append(completedCount).append(" | Total Rejected: ").append(rejectedCount).append("\n");

        String prompt = "You are an expert in child psychology and behavior tracking. Analyze this child's active habits and past commitment log history:\n\n" +
                childMetrics.toString() + "\n" +
                "Provide a comprehensive pedagogical analysis report for the parent. Respond ONLY with a raw JSON object containing:\n" +
                "1. 'behavioralScore': Integer (Score out of 100 reflecting long-term consistency).\n" +
                "2. 'strengths': Clear text explaining what the child is mastering.\n" +
                "3. 'behavioralIssues': Analysis of any laziness or drop in commitment.\n" +
                "4. 'parentActionPlan': 2 specific expert pedagogical recommendations for the parent to motivate the child.\n" +
                "Respond ONLY with raw JSON.";

        return aiService.callClaudeApi(prompt);
    }

    public String recommendChildRewards(Integer parentId, Integer childId) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");

        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");
        if (!parent.getChildren().contains(child)) throw new ApiException("This is not your child");

        String prompt = "Suggest 3 smart rewards/incentives for a child with the following profile:\n" +
                "- Age: " + child.getAge() + " years old\n" +
                "- Current Balance: " + child.getPoints() + " points\n\n" +
                "The rewards should be educational, experiential, or small treats appropriate for their age. " +
                "For each reward specify a reasonable requiredPoints value considering their balance.\n" +
                "Respond ONLY with a raw JSON array of objects. Each object must have: 'title', 'description', and 'suggestedPoints'.\n" +
                "Respond ONLY with raw JSON.";

        return aiService.callClaudeApi(prompt);
    }

    public String FamilyDisciplineScore(Integer parentId) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");
        return aiService.callClaudeApi(aiService.buildPromptFamilyScore(parent));
    }

    public String getFamilyActivity(Integer parentId , String city ) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");
        String weatherInfo = weatherService.getWeatherDescription(city );
        return aiService.callClaudeApi(aiService.buildPromptFamilyActivity(parent , weatherInfo));
    }
    public  List<Map<String, Object>> getFamilyLeaderboard(Integer parentId){
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");
        List<Child> children = new ArrayList<>(parent.getChildren());
        children.sort((a, b) -> b.getPoints() - a.getPoints());

        List<Map<String, Object>> leaderboard = new ArrayList<>();

        int rank = 1;
        for (Child child : children) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("rank", rank);
            entry.put("childName", child.getFullName());
            entry.put("points", child.getPoints());
            leaderboard.add(entry);
            rank++;
        }

        return leaderboard;
    }
}
    public String FamilyDisciplineScore(Integer parentId) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");
        return aiService.callClaudeApi(aiService.buildPromptFamilyScore(parent));
    }
}





