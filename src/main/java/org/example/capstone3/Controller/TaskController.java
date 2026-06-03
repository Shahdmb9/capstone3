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

    @PostMapping("/add")
    public ResponseEntity<?> addTask(@RequestBody @Valid TaskDTOIn taskIn) {
        taskService.addTask(taskIn);
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
}
