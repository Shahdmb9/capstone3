package org.example.capstone3.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.capstone3.Models.Profile;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualDTOOut {
    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Integer points;
    private Profile profile;
    private Set<String> badgeTitles;
    private Set<String> categoryNames;

}
