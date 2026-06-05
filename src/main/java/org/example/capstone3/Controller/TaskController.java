package org.example.capstone3.Controller;


import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.TaskDTOIn;
import org.example.capstone3.Service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTask() {
        return ResponseEntity.status(200).body(taskService.getAllTask());

    }

    @PostMapping("/add/{parentId}")
    public ResponseEntity<?> addTask(@PathVariable Integer parentId,@RequestBody @Valid TaskDTOIn taskIn) {
        taskService.addTask(parentId,taskIn);
        return ResponseEntity.status(200).body(new ApiResponse("task added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Integer id, @RequestBody @Valid TaskDTOIn taskIn) {
        taskService.updateTask(id, taskIn);
        return ResponseEntity.status(200).body(new ApiResponse("task updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(200).body(new ApiResponse("task deleted successfully"));
    }
    private final org.example.capstone3.Service.TaskApplicationService taskApplicationService;


    @PostMapping("/apply/{childId}/{taskId}")
    public ResponseEntity<ApiResponse> childCompleteChallenge(@PathVariable Integer childId, @PathVariable Integer taskId) {
        taskApplicationService.childApplyForTask(childId, taskId);
        return ResponseEntity.status(201).body(new ApiResponse("Challenge execution submitted! Waiting for Parent verification"));
    }

    @PutMapping("/approve/{applicationId}/{action}")
    public ResponseEntity<ApiResponse> parentVerifyWinner(@PathVariable Integer applicationId, @PathVariable String action) {
        taskApplicationService.parentApproveTaskWinner(applicationId, action);
        return ResponseEntity.status(200).body(new ApiResponse("Application processed. Winner verified and prize claimed"));
    }

}
