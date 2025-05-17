package com.bridge.medic.appointment.dto.request;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CreateAppointmentRequest {
    private Long specialistId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String description;
    private String summary;
    private String doctorType;
}