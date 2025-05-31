package com.bridge.medic.appointment.dto.request;

import com.bridge.medic.appointment.AppointmentStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private Long appointmentId;
    private AppointmentStatus newStatus;
}