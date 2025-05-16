package com.bridge.medic.specialist.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SpecialistDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String login;
    private LocalDateTime registrationDate;
    private String imageUrl;
//    private List<String> certifications;
}
