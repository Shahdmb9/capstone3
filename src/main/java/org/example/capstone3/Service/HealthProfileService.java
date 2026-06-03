package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.HealthProfile;
import org.example.capstone3.Models.Individual;
import org.example.capstone3.Repository.HealthProfileRepository;
import org.example.capstone3.Repository.IndividualRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthProfileService {

    private final HealthProfileRepository healthProfileRepository;
    private final IndividualRepository individualRepository;

    public List<HealthProfile> getAllProfiles() {
        return healthProfileRepository.findAll();
    }

    public void addProfile(HealthProfile profile, Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);

        if (individual == null) {
            throw new ApiException("Individual not found");
        }

        HealthProfile existingProfile = healthProfileRepository.findHealthProfileById(individualId);
        if (existingProfile != null) {
            throw new ApiException("Health Profile already exists for this individual");
        }

        profile.setIndividual(individual);
        healthProfileRepository.save(profile);
    }

    public void updateProfile(Integer individualId, HealthProfile newProfile) {
        HealthProfile oldProfile = healthProfileRepository.findHealthProfileById(individualId);

        if (oldProfile == null) {
            throw new ApiException("Profile not found");
        }

        oldProfile.setWeight(newProfile.getWeight());
        oldProfile.setHeight(newProfile.getHeight());
        oldProfile.setAge(newProfile.getAge());
        oldProfile.setMedicalConditions(newProfile.getMedicalConditions());
        oldProfile.setMainGoal(newProfile.getMainGoal());

        oldProfile.setIndividual(oldProfile.getIndividual());

        healthProfileRepository.save(oldProfile);
    }

    public void deleteProfile(Integer individualId) {
        HealthProfile profile = healthProfileRepository.findHealthProfileById(individualId);

        if (profile == null) {
            throw new ApiException("Profile not found");
        }

        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual != null) {
            individual.setHealthProfile(null);
            individualRepository.save(individual);
        }

        healthProfileRepository.delete(profile);
    }
}