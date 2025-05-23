package com.bridge.medic.appointment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDto {

    private Long id;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private String status;
    private String description;
    private String summary;
    private String meetingLink;
    private String attachedDocumentUrl;

}