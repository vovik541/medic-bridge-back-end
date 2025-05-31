package com.bridge.medic.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class AvailableSlotDto {
    private OffsetDateTime start;
    private OffsetDateTime end;
}