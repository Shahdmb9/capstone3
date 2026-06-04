package org.example.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.BadgeDTOin;
import org.example.capstone3.Models.Badge;
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
    public ResponseEntity<?> getAllBadges() {
        return ResponseEntity.status(200).body(badgeService.getAllBadge());
    }
}
