package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.BadgeDTOin;
import org.example.capstone3.DTO.Out.BadgeDTOOut;
import org.example.capstone3.Models.Badge;
import org.example.capstone3.Models.Individual;
import org.example.capstone3.Repository.BadgeRepository;
import org.example.capstone3.Repository.IndividualRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final ModelMapper modelMapper;
    private final IndividualRepository individualRepository;

    public List<BadgeDTOOut> getAllBadge () {
        List<BadgeDTOOut> badgeDTOOuts = new ArrayList<>();
        for (Badge b : badgeRepository.findAll()) {
            badgeDTOOuts.add(modelMapper.map(b, BadgeDTOOut.class));
        }
        return badgeDTOOuts;
    }

    public void addBadge (BadgeDTOin badgeDTOin) {
        Badge badge = modelMapper.map(badgeDTOin, Badge.class);
        if (badge == null) throw new ApiException("Badge translation failed");
        badgeRepository.save(badge);
    }

    public BadgeDTOOut getBadgeById(Integer id) {
        Badge badge = badgeRepository.findBadgeById(id);
        if (badge == null) throw new ApiException("Badge not found");
        return modelMapper.map(badge, BadgeDTOOut.class);
    }

    public void updateBadge(Integer id, BadgeDTOin badgeDTOin) {
        Badge oldBadge = badgeRepository.findBadgeById(id);
        if (oldBadge == null) throw new ApiException("Badge not found");

        oldBadge.setTitle(badgeDTOin.getTitle());
        oldBadge.setDescription(badgeDTOin.getDescription());
        oldBadge.setPointsRequired(badgeDTOin.getPointsRequired());

        badgeRepository.save(oldBadge);
    }

    public void deleteBadge(Integer id) {
        Badge badge = badgeRepository.findBadgeById(id);
        if (badge == null) throw new ApiException("Badge not found");
        badgeRepository.delete(badge);
    }


}
