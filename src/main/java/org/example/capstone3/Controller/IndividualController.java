package org.example.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.Models.Profile;
import org.example.capstone3.Models.Individual;
import org.example.capstone3.Models.Profile;
import org.example.capstone3.Service.IndividualService;
import org.example.capstone3.Service.ProfileService;
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
    public ResponseEntity<ApiResponse> registerIndividual(@RequestBody Individual individual) {

        individualService.addIndividual(individual);
        return ResponseEntity.status(201).body(new ApiResponse("Individual registered successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateIndividual(@PathVariable Integer id, @RequestBody Individual individual) {
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


    @PostMapping("/profile/add/{individualId}")
    public ResponseEntity<ApiResponse> addHealthProfile(@RequestBody @Valid Profile profile, @PathVariable Integer individualId) {
        healthProfileService.addProfile(profile, individualId);
        return ResponseEntity.status(201).body(new ApiResponse("Health Profile added successfully"));
    }

    @PutMapping("/profile/update/{individualId}")
    public ResponseEntity<ApiResponse> updateHealthProfile(@PathVariable Integer individualId, @RequestBody Profile profile) {
        healthProfileService.updateProfile(individualId, profile);
        return ResponseEntity.status(200).body(new ApiResponse("Health Profile updated successfully"));
    }

    @DeleteMapping("/profile/delete/{individualId}")
    public ResponseEntity<ApiResponse> deleteHealthProfile(@PathVariable Integer individualId) {
        healthProfileService.deleteProfile(individualId);
        return ResponseEntity.status(200).body(new ApiResponse("Health Profile deleted successfully"));
    }
}