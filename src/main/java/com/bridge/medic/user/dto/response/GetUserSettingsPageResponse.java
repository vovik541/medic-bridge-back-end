package com.bridge.medic.user.dto.response;

import com.bridge.medic.user.dto.LocationDTO;
import com.bridge.medic.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserSettingsPageResponse {
    private LocationDTO location;
    private UserDto user;
}
