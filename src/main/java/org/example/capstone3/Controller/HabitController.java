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
        return ResponseEntity.status(200).body(habitService.getIndividualHabits(individualId));
    }
    @GetMapping("/get-parent-habit/{parentId}")
    public ResponseEntity<?> getParentHabits(@PathVariable Integer parentId) {
        return ResponseEntity.status(200).body(habitService.getParentHabits(parentId));
    }
    @GetMapping("/get-child-habit/{childId}")
    public ResponseEntity<?> getChildHabits(@PathVariable Integer childId) {
        return ResponseEntity.status(200).body(habitService.getChildHabits(childId));
    }

    @PostMapping("/add-habit-individual") // تعديل المسمى ليكون دقيقاً وموجهاً للمستقل
    public ResponseEntity<ApiResponse> addHabitIndividual(@RequestBody @Valid IndividualHabitDTO dto) {
        habitService.addHabitIndividual(dto);
        return ResponseEntity.status(201).body(new ApiResponse("Habit added successfully"));
    }

    @PostMapping("/add-habit-parent/{parentId}/{childId}")
    public ResponseEntity<ApiResponse> addHabitParent(@PathVariable Integer parentId,@PathVariable Integer childId, @RequestBody @Valid Habit habit) {
        habitService.addHabitParent(parentId,childId,habit);
        return ResponseEntity.status(201).body(new ApiResponse("Habit added successfully"));
    }



    @PutMapping("/update/{habitId}")
    public ResponseEntity<ApiResponse> updateHabit(@PathVariable Integer habitId, @RequestBody IndividualHabitDTO dto) {
        habitService.updateHabit(habitId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Habit updated successfully"));
    }

    @DeleteMapping("/delete/{habitId}")
    public ResponseEntity<ApiResponse> deleteHabit(@PathVariable Integer habitId) {
        habitService.deleteHabit(habitId);
        return ResponseEntity.status(200).body(new ApiResponse("Habit deleted successfully"));
    }


    @PostMapping("/complete/{habitId}")
    public ResponseEntity<ApiResponse> completeHabitToday(@PathVariable Integer habitId) {
        habitService.completeHabitToday(habitId);
        return ResponseEntity.status(200).body(new ApiResponse("Habit executed and points credited successfully"));
    }
}
