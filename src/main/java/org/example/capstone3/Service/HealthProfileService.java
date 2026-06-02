package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.IndividualHealthProfileDTO;
import org.example.capstone3.Models.HealthProfile;
import org.example.capstone3.Models.Individual;
import org.example.capstone3.Repository.HealthProfileRepository;
import org.example.capstone3.Repository.IndividualRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthProfileService {

    private final HealthProfileRepository healthProfileRepository;
    private final IndividualRepository individualRepository;

    public void saveOrUpdateIndividualProfile(IndividualHealthProfileDTO dto) {
        Individual individual = individualRepository.findIndividualById(dto.getIndividualId());
        if (individual == null) {
            throw new ApiException("Individual not found");
        }

        HealthProfile profile = healthProfileRepository.findByIndividualId(dto.getIndividualId());
        if (profile == null) {
            profile = new HealthProfile();
            profile.setIndividual(individual);
        }

        profile.setWeight(dto.getWeight());
        profile.setHeight(dto.getHeight());
        profile.setAge(dto.getAge());
        profile.setMedicalConditions(dto.getMedicalConditions());
        profile.setMainGoal(dto.getMainGoal());

        healthProfileRepository.save(profile);
    }
}
