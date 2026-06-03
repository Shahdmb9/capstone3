package org.example.capstone3.Controller;


import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.TaskDTOIn;
import org.example.capstone3.DTO.In.TaskRewardDTOIn;
import org.example.capstone3.Service.TaskRewardService;
import org.example.capstone3.Service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task-reward")
@RequiredArgsConstructor
public class TaskRewardController {
    private final TaskRewardService taskRewardService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTaskReward() {
        return ResponseEntity.status(200).body(taskRewardService.getAllTaskReward());

    }

    @PostMapping("/add/{taskId}")
    public ResponseEntity<?> addTaskReward(@PathVariable Integer taskId, @RequestBody @Valid TaskRewardDTOIn taskRewardDTOIn) {
        taskRewardService.addTaskReward(taskId, taskRewardDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("task Reward added successfully"));
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<?> updateTaskReward(@PathVariable Integer taskId, @RequestBody @Valid TaskRewardDTOIn taskRewardDTOIn) {
        taskRewardService.updateTaskReward(taskId, taskRewardDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("task Reward updated successfully"));
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> deleteTaskReward(@PathVariable Integer taskId) {
        taskRewardService.deleteTaskReward(taskId);
        return ResponseEntity.status(200).body(new ApiResponse("task Reward deleted successfully"));
    }
}
