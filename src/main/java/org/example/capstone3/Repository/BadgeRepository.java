package org.example.capstone3.Repository;


import org.example.capstone3.Models.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    Badge findBadgeById(Integer id);

    List<Badge> findByPointsRequiredLessThanEqual(Integer points);
}

