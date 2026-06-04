package org.example.capstone3.Repository;


import org.example.capstone3.Models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Profile findProfileById(Integer id);
    Profile findProfileByChildId(Integer childId);
}

