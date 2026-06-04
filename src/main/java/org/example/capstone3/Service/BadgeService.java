package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.BadgeDTOin;
import org.example.capstone3.DTO.Out.BadgeDTOOut;
import org.example.capstone3.Models.Badge;
import org.example.capstone3.Repository.BadgeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private  final ModelMapper modelMapper;
    public List<BadgeDTOOut> getAllBadge (){
        List<BadgeDTOOut> badgeDTOOuts = new ArrayList<>();
        for (Badge b : badgeRepository.findAll())
        {
            badgeDTOOuts.add(modelMapper.map(b, BadgeDTOOut.class));
        }
        return badgeDTOOuts;
    }
    public  void  addBadge (BadgeDTOin badgeDTOin){
        Badge badge = modelMapper.map(badgeDTOin, Badge.class);
        if (badge == null) throw new ApiException("badge not found");
        badgeRepository.save(badge);

    }

}
