package com.bridge.medic.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String login;
    private Boolean isLocked;
    private LocalDateTime registrationDate;

    private String imageUrl;

    private List<String> roles;
}