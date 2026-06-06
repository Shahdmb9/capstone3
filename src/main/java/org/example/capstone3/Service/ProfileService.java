package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.Profile;
import org.example.capstone3.Models.Individual;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Repository.ProfileRepository;
import org.example.capstone3.Repository.IndividualRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.example.capstone3.Repository.ChildRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final IndividualRepository individualRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    // ==========================================
    //  (Individual)
    // ==========================================

    public void addProfile(Profile profile, Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) {
            throw new ApiException("Individual not found");
        }

        Profile existingProfile = profileRepository.findProfileByIndividualId(individualId);
        if (existingProfile != null) {
            throw new ApiException("Health Profile already exists for this individual");
        }

        profile.setIndividual(individual);
        profileRepository.save(profile);
    }

    public void updateProfile(Integer individualId, Profile newProfile) {
        Profile oldProfile = profileRepository.findProfileByIndividualId(individualId);
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
        Profile profile = profileRepository.findProfileByIndividualId(individualId);
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

    // ==========================================
    // Child Profile
    // ==========================================

    public void addChildProfile(Integer parentId, Integer childId, Profile profile) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");

        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");

        if (!parent.getChildren().contains(child)) {
            throw new ApiException("This is not your child");
        }

        Profile existingProfile = profileRepository.findProfileByChildId(childId);
        if (existingProfile != null) {
            throw new ApiException("Health Profile already exists for this child");
        }

        profile.setChild(child);
        child.setProfile(profile);
        profileRepository.save(profile);
    }

    public void updateChildProfile(Integer parentId, Integer childId, Profile newProfile) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");

        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");

        if (!parent.getChildren().contains(child)) {
            throw new ApiException("This is not your child");
        }

        Profile oldProfile = profileRepository.findProfileByChildId(childId);
        if (oldProfile == null) {
            throw new ApiException("Profile not found for this child");
        }

        oldProfile.setWeight(newProfile.getWeight());
        oldProfile.setHeight(newProfile.getHeight());
        oldProfile.setAge(newProfile.getAge());
        oldProfile.setMedicalConditions(newProfile.getMedicalConditions());
        oldProfile.setMainGoal(newProfile.getMainGoal());

        profileRepository.save(oldProfile);
    }

    public void deleteChildProfile(Integer parentId, Integer childId) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");

        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");

        if (!parent.getChildren().contains(child)) {
            throw new ApiException("This is not your child");
        }

        Profile profile = profileRepository.findProfileByChildId(childId);
        if (profile == null) {
            throw new ApiException("Profile not found for this child");
        }

        child.setProfile(null);
        childRepository.save(child);
        profileRepository.delete(profile);
    }
}
