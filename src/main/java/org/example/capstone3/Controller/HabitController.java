//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package org.example.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.IndividualHabitDTO;
import org.example.capstone3.Models.Habit;
import org.example.capstone3.Service.HabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/habit")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @GetMapping("/get-individual-habits/{individualId}")
    public ResponseEntity<?> getIndividualHabits(@PathVariable Integer individualId) {
        return ResponseEntity.status(200).body(this.habitService.getIndividualHabits(individualId));
    }

    @GetMapping("/get-parent-habit/{parentId}")
    public ResponseEntity<?> getParentHabits(@PathVariable Integer parentId) {
        return ResponseEntity.status(200).body(this.habitService.getParentHabits(parentId));
    }

    @GetMapping("/get-child-habit/{childId}")
    public ResponseEntity<?> getChildHabits(@PathVariable Integer childId) {
        return ResponseEntity.status(200).body(this.habitService.getChildHabits(childId));
    }

    @PostMapping("/add-habit-individual/{individualId}")
    public ResponseEntity<ApiResponse> addHabitIndividual(@PathVariable Integer individualId, @RequestBody @Valid IndividualHabitDTO dto) {
        this.habitService.addHabitIndividual(individualId, dto);
        return ResponseEntity.status(201).body(new ApiResponse("Habit added successfully"));
    }

    @PostMapping("/add-habit-parent/{parentId}/{childId}")
    public ResponseEntity<ApiResponse> addHabitParent(@PathVariable Integer parentId, @PathVariable Integer childId, @RequestBody @Valid Habit habit) {
        this.habitService.addHabitParent(parentId, childId, habit);
        return ResponseEntity.status(201).body(new ApiResponse("Habit added successfully"));
    }

    @PutMapping("/update/{habitId}")
    public ResponseEntity<ApiResponse> updateHabit(@PathVariable Integer habitId, @RequestBody IndividualHabitDTO dto) {
        this.habitService.updateHabit(habitId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Habit updated successfully"));
    }

    @DeleteMapping("/delete/{habitId}")
    public ResponseEntity<ApiResponse> deleteHabit(@PathVariable Integer habitId) {
        this.habitService.deleteHabit(habitId);
        return ResponseEntity.status(200).body(new ApiResponse("Habit deleted successfully"));
    }

    /*
    @GetMapping("/get-ind-hapits-streak/{individualId}")
    public ResponseEntity<?> getIndividualHabit(@PathVariable Integer individualId) {
        return ResponseEntity.status(200).body(this.habitService.IndividualStreakPerHabit(individualId));
    }
    */

    @PutMapping("/complete-habit/{habitId}")
    public ResponseEntity<ApiResponse> completeHabit(@PathVariable Integer habitId) {
        this.habitService.logHabit(habitId);
        return ResponseEntity.status(200).body(new ApiResponse("Habit completed successfully"));
    }

    @PutMapping("/review-log-of-child/{parentId}/{habitId}/{status}")
    public ResponseEntity<ApiResponse> reviewHabit(@PathVariable Integer parentId, @PathVariable Integer habitId, @PathVariable String status) {
        this.habitService.reviewChildLog(parentId, habitId, status);
        return ResponseEntity.status(200).body(new ApiResponse("Habit reviewed and updated successfully"));
    }

    @GetMapping("/ai-habits/{individualId}")
    public ResponseEntity<?> generateHabits(@PathVariable Integer individualId){
        return ResponseEntity.status(200).body(this.habitService.generateHabits(individualId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getHabitsByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.status(200).body(habitService.getHabitsByCategory(categoryId));
    }

    @PutMapping("/accept-habit-suggested/{individualId}/{habitId}")
    public ResponseEntity<?> acceptHabitSuggestedByAI(@PathVariable Integer individualId, @PathVariable Integer habitId){
        habitService.acceptHabitSuggestedByAI(individualId, habitId);
        return ResponseEntity.status(200).body(new ApiResponse("Habit accepted successfully"));
    }

    @GetMapping("/get-ai-habit-suggested/{individualId}")
    public ResponseEntity<?> getAiSuggested(@PathVariable Integer individualId){
        return ResponseEntity.status(200).body(habitService.AISuggestedHabit(individualId));
    }

    @GetMapping("/ia_risk_prediction/{id}")
    public ResponseEntity<?> riskPrediction(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(habitService.riskPrediction(id));
    }

    @GetMapping("/ia_best_habit_time/{id}")
    public ResponseEntity<?> BestHabitTime(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(habitService.BestHabitTime(id));
    }

    @GetMapping("/{habitId}/ai/improvement")
    public ResponseEntity<String> getHabitImprovementAdvisor(@PathVariable Integer habitId) {
        return ResponseEntity.status(200).body(habitService.getHabitImprovementAdvisor(habitId));
    }

    @GetMapping("/{habitId}/ai/commitment")
    public ResponseEntity<String> getHabitCommitmentAnalysis(@PathVariable Integer habitId) {
        return ResponseEntity.status(200).body(habitService.getHabitCommitmentAnalysis(habitId));
    }
}