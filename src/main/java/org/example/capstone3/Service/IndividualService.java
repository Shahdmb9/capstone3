package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.IndividualDTOIn;
import org.example.capstone3.Models.*;
import org.example.capstone3.Repository.CategoryRepository;
import org.example.capstone3.Repository.HabitLogRepository;
import org.example.capstone3.Repository.IndividualRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndividualService {

    private final IndividualRepository individualRepository;
    private final CategoryRepository categoryRepository;
    private  final ModelMapper modelMapper;
    private final AiService aiService;
    private final HabitLogRepository habitLogRepository;

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



    //  اقتراح عادات بناءً على اهتمامات الشخص المستقل
    public String getAiHabitRecommendations(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        StringBuilder categoriesStr = new StringBuilder();
        for (Category cat : individual.getCategories()) {
            categoriesStr.append(cat.getName()).append(", ");
        }
        if (categoriesStr.isEmpty()) throw new ApiException("Please add interests first");

        String prompt = "Suggest 3 habits for categories: [" + categoriesStr + "]. Return JSON array with title, description, frequency, points.";

        return aiService.callClaudeApi(prompt);
    }

    //  بناء خطة هدف ذكية مخصصة بناءً على الملف الصحي للشخص
    public String getAiGoalPlan(Integer individualId, String userGoal) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        Profile profile = individual.getProfile();
        if (profile == null) throw new ApiException("Please complete your health profile first");

        String prompt = "Goal: " + userGoal + ". User Age: " + profile.getAge() + ", Weight: " + profile.getWeight() + ". Generate structured JSON plan.";

        return aiService.callClaudeApi(prompt);
    }

    //  Badge Progress Advisor ( الشارات القادمة وكم باقي عليها)
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

    //  Smart Habit Roadmap (خريطة طريق العادات المقترحة )
    public String getSmartHabitRoadmap(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        Profile profile = individual.getProfile();
        String mainGoal = (profile != null) ? profile.getMainGoal() : "General self-improvement";

        String prompt = "The user has the final goal: \"" + mainGoal + "\".\n" +
                "Create a long-term 'Smart Habit Roadmap' divided into progressive phases. For each phase, suggest specific habits, category, frequency, duration (e.g., '21 days'), and expected points.\n" +
                "Respond ONLY with a valid raw JSON object containing:\n" +
                "1. 'finalGoal': \"" + mainGoal + "\"\n" +
                "2. 'phases': Array of objects (phaseName, focus, recommendedHabits [array of objects with title, description, category, frequency, duration, expectedPoints]).\n" +
                "Respond ONLY with raw JSON.";

        return aiService.callClaudeApi(prompt);
    }





}

