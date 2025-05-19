package com.bridge.medic.appointment.dto;

import com.bridge.medic.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ConsultationDto {
    private Long id;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private String status;
    private String description;
    private String summary;
    private UserDto doctor;
    private String meetingLink;
    private String attachedDocumentUrl;
    private String comment;
}
