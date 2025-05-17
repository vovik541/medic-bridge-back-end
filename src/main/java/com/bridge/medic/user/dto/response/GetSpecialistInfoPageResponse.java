package com.bridge.medic.user.dto.response;

import com.bridge.medic.specialist.dto.SpecialistDto;
import lombok.Data;

import java.util.List;

@Data
public class GetSpecialistInfoPageResponse {
    private SpecialistDto specialist;
    private List<String> approvedPositions;
}
