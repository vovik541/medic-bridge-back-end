package com.bridge.medic.appointment.dto.response;

import com.bridge.medic.appointment.dto.ConsultationDto;
import lombok.Data;

import java.util.List;

@Data
public class GetUserConsultationsResponse {
    private List<ConsultationDto> consultations;
}
