package com.bridge.medic.specialist.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class RescheduleAppointmentRequest {
    private Long appointmentId;
    private OffsetDateTime newStart;
    private OffsetDateTime newEnd;
}