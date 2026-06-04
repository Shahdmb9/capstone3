package org.example.capstone3.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.RewardDTOIn;
import org.example.capstone3.Models.Reward;
import org.example.capstone3.Service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reward")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;


    @GetMapping("/get")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(rewardService.getAllRewards());
    }

    @PostMapping("/add/{parentId}/{habitId}")
    public ResponseEntity<?> add( @PathVariable Integer parentId,@PathVariable Integer habitId,@RequestBody @Valid RewardDTOIn reward){
        rewardService.add(parentId,habitId,reward);
        return ResponseEntity.status(200).body(new ApiResponse("Reward added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,@RequestBody @Valid RewardDTOIn reward){
        rewardService.update(id,reward);
        return ResponseEntity.status(200).body(new ApiResponse("Reward updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        rewardService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Reward deleted successfully"));
    }


}
