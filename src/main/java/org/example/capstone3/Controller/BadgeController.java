package org.example.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.BadgeDTOin;
import org.example.capstone3.DTO.Out.BadgeDTOOut;
import org.example.capstone3.Service.BadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/badge")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createBadge(@RequestBody @Valid BadgeDTOin badge) {
        badgeService.addBadge(badge);
        return ResponseEntity.status(201).body(new ApiResponse("New Badge created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<BadgeDTOOut>> getAllBadges() {
        return ResponseEntity.status(200).body(badgeService.getAllBadge());
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<BadgeDTOOut> getBadgeById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(badgeService.getBadgeById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateBadge(@PathVariable Integer id, @RequestBody @Valid BadgeDTOin badgeDTOin) {
        badgeService.updateBadge(id, badgeDTOin);
        return ResponseEntity.status(200).body(new ApiResponse("Badge updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteBadge(@PathVariable Integer id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.status(200).body(new ApiResponse("Badge deleted successfully"));
    }


}
