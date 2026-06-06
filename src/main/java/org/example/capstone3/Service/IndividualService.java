package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.IndividualDTOIn;
import org.example.capstone3.DTO.Out.BadgeDTOOut;
import org.example.capstone3.Models.*;
import org.example.capstone3.Repository.CategoryRepository;
import org.example.capstone3.Repository.HabitLogRepository;
import org.example.capstone3.Repository.IndividualRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IndividualService {

    private final IndividualRepository individualRepository;
    private final CategoryRepository categoryRepository;
    private  final ModelMapper modelMapper;
    private final AiService aiService;
    private final HabitLogRepository habitLogRepository;
    private final CreatePdfService createPdfService;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;


    public List<Individual> getAllIndividuals() {
        return individualRepository.findAll();
    }

    public void addIndividual(IndividualDTOIn individualIn) {

        Individual existing = individualRepository.findIndividualByEmail(individualIn.getEmail());
        if (existing != null) {
            throw new ApiException("Email already registered");
        }

        Individual individual = modelMapper.map(individualIn, Individual.class);
        individual.setPoints(0);
        individualRepository.save(individual);
    }


    public void updateIndividual(Integer id, IndividualDTOIn newIndividualIn)
        {
        Individual oldIndividual = individualRepository.findIndividualById(id);
        if (oldIndividual == null) {
            throw new ApiException("Individual not found");
        }
        Individual newIndividual =  modelMapper.map(newIndividualIn,Individual.class);
        oldIndividual.setFullName(newIndividual.getFullName());
        oldIndividual.setPassword(newIndividual.getPassword());
        oldIndividual.setPhoneNumber(newIndividual.getPhoneNumber());
        individualRepository.save(oldIndividual);
    }

    public void deleteIndividual(Integer id) {
        Individual individual = individualRepository.findIndividualById(id);
        if (individual == null) {
            throw new ApiException("Individual not found");
        }
        individualRepository.delete(individual);
    }

    public void addInterest (Integer individualId, Integer categoryId){
        Individual individual = individualRepository.findIndividualById(individualId);
        Category category = categoryRepository.findCategoryById(categoryId);

        if (individual == null)
            throw new ApiException("Individual not found");
        if (category == null)
            throw new ApiException("Category not found");

        individual.getCategories().add(category);
        category.getIndividual().add(individual);

        individualRepository.save(individual);
    }

    public String generateGoalPlan(Integer individualId, String userGoal) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        Profile profile = individual.getProfile();
        if (profile == null) throw new ApiException("Please complete your health profile first");

        StringBuilder currentHabits = new StringBuilder();
        for (Habit h : individual.getHabits()) {
            currentHabits.append("- ").append(h.getTitle()).append("\n");
        }

        String prompt = "User Goal: \"" + userGoal + "\"\n" +
                "User Profile Details:\n" +
                "- Age: " + profile.getAge() + "\n" +
                "- Weight: " + profile.getWeight() + " kg\n" +
                "- Height: " + profile.getHeight() + " cm\n" +
                "- Medical Conditions: " + profile.getMedicalConditions() + "\n" +
                "- Bad Habits to break: " + profile.getBadHabit() + "\n\n" +
                "Current Active Habits:\n" + currentHabits + "\n" +
                "Generate a structured fitness/lifestyle plan tailored strictly to these metrics. Provide a JSON object containing:\n" +
                "1. 'summary': 2-3 sentences explaining the strategy.\n" +
                "2. 'recommendedHabits': Array of new habits to add (title, description, frequency).\n" +
                "3. 'warnings': Any health/medical precautions based on their profile.\n" +
                "Respond ONLY with raw JSON.";

        return aiService.callClaudeApi(prompt);
    }


    public String getAchievementIndex(Integer individualId, String period) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        LocalDate now = LocalDate.now();
        LocalDate startDate;

        if ("week".equalsIgnoreCase(period)) startDate = now.minusDays(7);
        else if ("month".equalsIgnoreCase(period)) startDate = now.minusMonths(1);
        else startDate = now.minusYears(1);

        int totalLogs = 0;
        int completedLogs = 0;

        for (Habit habit : individual.getHabits()) {
            List<HabitLog> logs = habitLogRepository.findHabitLogByHabitIdAndLoggedDateBetweenAndApprovalStatus(
                    habit.getId(), startDate, now, "COMPLETED");
            completedLogs += logs.size();

            totalLogs += ("week".equalsIgnoreCase(period)) ? 7 : 30;
        }

        double completionRate = (totalLogs == 0) ? 0.0 : ((double) completedLogs / totalLogs) * 100;

        String prompt = "The user completed " + completedLogs + " habit logs in the past " + period + ".\n" +
                "Actual Completion Rate: " + String.format("%.2f", completionRate) + "%.\n" +
                "Please evaluate this performance and respond ONLY with a raw JSON object containing:\n" +
                "1. 'score': Performance score out of 100.\n" +
                "2. 'evaluation': Clear assessment of their commitment (2 sentences).\n" +
                "3. 'motivationalMessage': A custom personalized psychological boost or warning based on their score.\n" +
                "Respond ONLY with raw JSON.";

        return aiService.callClaudeApi(prompt);
    }

    public String getBadgeProgressAdvisor(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        StringBuilder earnedBadges = new StringBuilder();
        for (Badge b : individual.getBadges()) {
            earnedBadges.append("- ").append(b.getTitle()).append("\n");
        }

        String prompt = "User: " + individual.getFullName() + "\n" +
                "Current Total Points: " + individual.getPoints() + "\n" +
                "Already Earned Badges:\n" + earnedBadges + "\n" +
                "Review the current points and analyze which badges are upcoming (Standard milestones: Starter=100, Committed=500, Hero=1000, Legend=2500). " +
                "Calculate exactly how many points are remaining for each locked badge, and give 2 tailored tips on how to reach them faster.\n" +
                "Respond ONLY with a raw JSON object containing:\n" +
                "1. 'currentPoints': Integer\n" +
                "2. 'upcomingBadges': Array of objects (badgeTitle, requiredPoints, pointsRemaining)\n" +
                "3. 'advisorTips': Array of 2 personalized text suggestions.\n" +
                "Respond ONLY with raw JSON.";

        return aiService.callClaudeApi(prompt);
    }

    public String getSmartHabitRoadmap(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) {
            throw new ApiException("Individual not found");
        }

        Profile profile = individual.getProfile();
        String mainGoal = (profile != null) ? profile.getMainGoal() : "General self-improvement";

        String prompt = "Create a long-term 'Smart Habit Roadmap' divided into progressive phases for goal: \"" + mainGoal + "\" in JSON object format. " +
                "Provide specific habits, category, and expected points for each phase. Respond ONLY with raw JSON.";

        return aiService.callClaudeApi(prompt);
    }

    public void sendIndividualRoadmapReport(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        Profile profile = individual.getProfile();
        String mainGoal = (profile != null) ? profile.getMainGoal() : "General self-improvement";


        String prompt = "Create a progressive 'Smart Habit Roadmap' for goal: \"" + mainGoal + "\" divided into 3 phases in JSON format. " +
                "The JSON MUST strictly use these keys: 'goal', 'phases', 'phase_name', 'duration_weeks', 'habits', 'habit_name', 'description', 'category', 'points_per_completion'. Respond ONLY with raw JSON.";

        String rawJsonRoadmap = aiService.callClaudeApi(prompt);

        byte[] pdfBytes = createPdfService.generateAiRoadmapPdf(individual.getFullName(), rawJsonRoadmap);

        emailService.sendReportByEmail(individual.getEmail(), pdfBytes, individual.getFullName(), "Smart Habit Roadmap");
    }
    public Set<Badge> getIndividualBadges(Integer individualId) {
        Individual individual = individualRepository.findById(individualId)
                .orElseThrow(() -> new ApiException("Individual not found"));

        return individual.getBadges();
    }







}

