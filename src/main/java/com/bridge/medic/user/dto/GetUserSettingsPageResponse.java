package com.bridge.medic.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserSettingsPageResponse {
    private LocationDTO location;
    private UserDto user;
}
