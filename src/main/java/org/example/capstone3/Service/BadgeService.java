package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.Badge;
import org.example.capstone3.Repository.BadgeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public void addBadge(Badge badge) {
        Badge existing = badgeRepository.findBadgeById(badge.getId());
        if (existing != null) {
            throw new ApiException("Badge already exists");
        }
        badgeRepository.save(badge);
    }
}
