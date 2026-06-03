package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.Profile;
import org.example.capstone3.Models.Individual;
import org.example.capstone3.Repository.ProfileRepository;
import org.example.capstone3.Repository.IndividualRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final IndividualRepository individualRepository;

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public void addProfile(Profile profile, Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);

        if (individual == null) {
            throw new ApiException("Individual not found");
        }

        Profile existingProfile = profileRepository.findProfileById(individualId);
        if (existingProfile != null) {
            throw new ApiException("Health Profile already exists for this individual");
        }

        profile.setIndividual(individual);
        profileRepository.save(profile);
    }

    public void updateProfile(Integer individualId, Profile newProfile) {
        Profile oldProfile = profileRepository.findProfileById(individualId);
        if (oldProfile == null) {
            throw new ApiException("Profile not found");
        }

        oldProfile.setWeight(newProfile.getWeight());
        oldProfile.setHeight(newProfile.getHeight());
        oldProfile.setAge(newProfile.getAge());
        oldProfile.setMedicalConditions(newProfile.getMedicalConditions());
        oldProfile.setMainGoal(newProfile.getMainGoal());

        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual != null) {
            oldProfile.setIndividual(individual);
            individual.setProfile(oldProfile);
        }

        profileRepository.save(oldProfile);
    }


    public void deleteProfile(Integer individualId) {
        Profile profile = profileRepository.findProfileById(individualId);

        if (profile == null) {
            throw new ApiException("Profile not found");
        }

        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual != null) {
            individual.setProfile(null);
            individualRepository.save(individual);
        }

        profileRepository.delete(profile);
    }
}