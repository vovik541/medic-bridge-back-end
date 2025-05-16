package com.bridge.medic.user.dto.response;

import com.bridge.medic.specialist.dto.SpecialistDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetDoctorsFromSearchResponse {
    private List<SpecialistDto> specialists;
}
