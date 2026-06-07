package org.example.capstone3.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.IndividualDTOIn;
import org.example.capstone3.DTO.OUT.ReplacementHabit;
import org.example.capstone3.DTO.OUT.SmartHabit;
import org.example.capstone3.DTO.OUT.SmartHabitsResponse;
import org.example.capstone3.DTO.Out.BadgeDTOOut;
import org.example.capstone3.Models.*;
import org.example.capstone3.Repository.*;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final BadgeRepository badgeRepository;


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

        List<Badge> allSystemBadges = badgeRepository.findAll();
        StringBuilder systemBadgesSchema = new StringBuilder();
        for (Badge b : allSystemBadges) {
            systemBadgesSchema.append("- ").append(b.getTitle())
                    .append(" (Required Points: ").append(b.getPointsRequired()).append(")\n");
        }

        String prompt = "User: " + individual.getFullName() + "\n" +
                "Current Total Points: " + individual.getPoints() + "\n" +
                "Already Earned Badges:\n" + earnedBadges + "\n" +
                "Available System Badges & Milestones (From Database):\n" + systemBadgesSchema + "\n" +
                "Review the user's current points against the system badges available in the database. " +
                "Identify which badges are still locked, calculate exactly how many points are remaining for each locked badge, " +
                "and give 2 tailored tips on how to reach the next locked milestone faster.\n" +
                "Respond ONLY with a raw JSON object containing:\n" +
                "1. 'currentPoints': Integer\n" +
                "2. 'upcomingBadges': Array of objects (badgeTitle, requiredPoints, pointsRemaining)\n" +
                "3. 'advisorTips': Array of 2 personalized text suggestions.\n" +
                "Respond ONLY with raw JSON.";

        return aiService.callClaudeApi(prompt);
    }

    public SmartHabitsResponse getAiAdvice(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) {
            throw new ApiException("Individual not found");
        }

        Profile profile = individual.getProfile();
        String mainGoal =profile.getMainGoal();
        String badHabits = profile.getBadHabit();
        String prompt="";
        ObjectMapper mapper = new ObjectMapper();

        if(mainGoal!=null)
             prompt = "Create a 'Smart Habit' to achieve goal: \"" + mainGoal + "\" in JSON object format. " +
                "Provide specific habits in this format \n"+
                     "1. 'title': e.g., 'Exercise'\n" +
                     "2. 'description': e.g., 'Regular exercise for 30 minutes'\n"
                     +", and expected points for each phase. Respond ONLY with raw JSON.";
        else
            prompt = "Create a 'Smart Habit ' to get red of my bad habit : \"" + badHabits + "\" in JSON object format. " +
                    "Provide specific habits in this format\"smartHabits\" : [ { \"title\" : e.g \"Stop smoking\",\n" +
                    "  \"description\" : \"Break the habit of nighttime eating by establishing a structured evening routine and healthier alternatives\",\n" +
                    "  \"targetBadHabit : e.g eating at night\",\n" +
                    "  \"replacementHabits\" : [ {\n"+
                    "1. 'title': e.g., 'Exercise'\n" +
                    "2. 'description': e.g., 'Regular exercise for 30 minutes  }] }]}'\n"
                    +", Respond ONLY with raw JSON.";

        String responseAi = aiService.callClaudeApi(prompt);
        ObjectMapper objectMapper = new ObjectMapper();
        SmartHabitsResponse response =null;
        try {

            //get the json string from the response and map it to my custom DTO
             response = objectMapper.readValue(responseAi, SmartHabitsResponse.class);

        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return response;
    }


    public Set<Badge> getIndividualBadges(Integer individualId) {
        Individual individual = individualRepository.findById(individualId)
                .orElseThrow(() -> new ApiException("Individual not found"));

        return individual.getBadges();
    }







}

