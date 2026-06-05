package org.example.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.Models.Profile;
import org.example.capstone3.Service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.status(200).body(profileService.getAllProfiles());
    }

    // =========================================================================
    // 1 (Individual Health Profile Endpoints)
    // =========================================================================

    @PostMapping("/add-individual/{individualId}")
    public ResponseEntity<ApiResponse> addProfile(@RequestBody @Valid Profile profile, @PathVariable Integer individualId) {
        profileService.addProfile(profile, individualId);
        return ResponseEntity.status(201).body(new ApiResponse("Individual Health Profile added successfully"));
    }

    @PutMapping("/update-individual/{individualId}")
    public ResponseEntity<ApiResponse> updateProfile(@PathVariable Integer individualId, @RequestBody @Valid Profile newProfile) {
        profileService.updateProfile(individualId, newProfile);
        return ResponseEntity.status(200).body(new ApiResponse("Individual Health Profile updated successfully"));
    }

    @DeleteMapping("/delete-individual/{individualId}")
    public ResponseEntity<ApiResponse> deleteProfile(@PathVariable Integer individualId) {
        profileService.deleteProfile(individualId);
        return ResponseEntity.status(200).body(new ApiResponse("Individual Health Profile deleted successfully"));
    }

    // =========================================================================
    // 2 (Child Health Profile Endpoints)
    // =========================================================================

    @PostMapping("/add-child/{parentId}/{childId}")
    public ResponseEntity<ApiResponse> addChildProfile(@PathVariable Integer parentId, @PathVariable Integer childId, @RequestBody @Valid Profile profile) {
        profileService.addChildProfile(parentId, childId, profile);
        return ResponseEntity.status(201).body(new ApiResponse("Child Health Profile added successfully"));
    }

    @PutMapping("/update-child/{parentId}/{childId}")
    public ResponseEntity<ApiResponse> updateChildProfile(@PathVariable Integer parentId, @PathVariable Integer childId, @RequestBody @Valid Profile newProfile) {
        profileService.updateChildProfile(parentId, childId, newProfile);
        return ResponseEntity.status(200).body(new ApiResponse("Child Health Profile updated successfully"));
    }

    @DeleteMapping("/delete-child/{parentId}/{childId}")
    public ResponseEntity<ApiResponse> deleteChildProfile(@PathVariable Integer parentId, @PathVariable Integer childId) {
        profileService.deleteChildProfile(parentId, childId);
        return ResponseEntity.status(200).body(new ApiResponse("Child Health Profile deleted successfully"));
    }
}
