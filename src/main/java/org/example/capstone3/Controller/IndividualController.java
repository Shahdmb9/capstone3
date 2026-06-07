package org.example.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.IndividualDTOIn;
import org.example.capstone3.DTO.Out.BadgeDTOOut;
import org.example.capstone3.Models.Profile;
import org.example.capstone3.Service.IndividualService;
import org.example.capstone3.Service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/individual")
@RequiredArgsConstructor
public class IndividualController {

    private final IndividualService individualService;
    private final ProfileService healthProfileService;


    @GetMapping("/get")
    public ResponseEntity<?> getAllIndividuals() {
        return ResponseEntity.status(200).body(individualService.getAllIndividuals());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> registerIndividual(@RequestBody @Valid IndividualDTOIn individual) {
        individualService.addIndividual(individual);
        return ResponseEntity.status(201).body(new ApiResponse("Individual registered successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateIndividual(@PathVariable Integer id, @RequestBody @Valid IndividualDTOIn individual) {
        individualService.updateIndividual(id, individual);
        return ResponseEntity.status(200).body(new ApiResponse("Individual updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteIndividual(@PathVariable Integer id) {
        individualService.deleteIndividual(id);
        return ResponseEntity.status(200).body(new ApiResponse("Individual deleted successfully"));
    }

    @PutMapping("/add-interest/{individualId}/{categoryId}")
    public ResponseEntity<ApiResponse> addInterest(@PathVariable Integer individualId, @PathVariable Integer categoryId) {
        individualService.addInterest(individualId, categoryId);
        return ResponseEntity.status(200).body(new ApiResponse("Interest added successfully"));
    }

    @PostMapping("/{individualId}/ai/generate-plan/{userGoal}")
    public ResponseEntity<String> generateGoalPlan(@PathVariable Integer individualId, @PathVariable String userGoal) {
        return ResponseEntity.status(200).body(individualService.generateGoalPlan(individualId, userGoal));
    }


    @GetMapping("/{individualId}/ai/achievement-index")
    public ResponseEntity<String> getAchievementIndex(@PathVariable Integer individualId, @RequestParam String period) {
        return ResponseEntity.status(200).body(individualService.getAchievementIndex(individualId, period));
    }

    @GetMapping("/{individualId}/ai/badges-progress")
    public ResponseEntity<String> getBadgeProgressAdvisor(@PathVariable Integer individualId) {
        return ResponseEntity.status(200).body(individualService.getBadgeProgressAdvisor(individualId));
    }

    @GetMapping("/{individualId}/ai/advice")
    public ResponseEntity<?> getSmartHabitRoadmap(@PathVariable Integer individualId) {
        return ResponseEntity.status(200).body(individualService.getAiAdvice(individualId));
    }


    @GetMapping("/my-badges/{individualId}")
    public ResponseEntity<?> getMyBadges(@PathVariable Integer individualId) {
        return ResponseEntity.status(200).body(individualService.getIndividualBadges(individualId));
    }







}