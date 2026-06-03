package org.example.capstone3.Controller;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.IndividualHabitDTO;
import org.example.capstone3.Service.HabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/habit")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;


    @GetMapping("/get/{individualId}")
    public ResponseEntity<?> getMyHabits(@PathVariable Integer individualId) {
        return ResponseEntity.status(200).body(habitService.getIndividualHabits(individualId));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addHabit(@RequestBody IndividualHabitDTO dto) {
        habitService.addHabit(dto);
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
