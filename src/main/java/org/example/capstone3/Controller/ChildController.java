package org.example.capstone3.Controller;

import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.ChildDtoIn;
import org.example.capstone3.Models.Reward;
import org.example.capstone3.Models.TaskReward;
import org.example.capstone3.Service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {
    private  final ChildService childService ;
    @GetMapping("/all")
    public ResponseEntity<?> getAllChild() {
        return ResponseEntity.status(200).body(childService.getAllChildren());

    }
    @PostMapping("/add/{parentId}")
    public ResponseEntity<?> addChild(@PathVariable Integer parentId, @RequestBody @Valid ChildDtoIn childDtoIn ) {
        childService.addChild(parentId, childDtoIn);
        return ResponseEntity.status(200).body(new ApiResponse("child added successfully"));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateChild(@PathVariable Integer id, @RequestBody @Valid ChildDtoIn childDtoIn ) {

        childService.updateChild(id, childDtoIn);
        return ResponseEntity.status(200).body(new ApiResponse("child updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteChild(@PathVariable Integer id) {
        childService.deleteChild(id);
        return ResponseEntity.status(200).body(new ApiResponse("child deleted successfully"));
    }
    @GetMapping("/my-rewards/{childId}")
    public ResponseEntity<List<Reward>> getMyRewards(@PathVariable Integer childId) {
        return ResponseEntity.status(200).body(childService.getChildClaimedRewards(childId));
    }

    @GetMapping("/my-task-rewards/{childId}")
    public ResponseEntity<List<TaskReward>> getMyTaskRewards(@PathVariable Integer childId) {
        return ResponseEntity.status(200).body(childService.getChildClaimedTaskRewards(childId));
    }

    @GetMapping("/available-challenges/{childId}")
    public ResponseEntity<?> getAvailableParentTasks(@PathVariable Integer childId) {
        return ResponseEntity.status(200).body(childService.getAvailableParentTasks(childId));
    }





}
