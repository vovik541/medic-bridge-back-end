package com.bridge.medic.specialist.dto;

import com.bridge.medic.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ConsultationForDoctorDto {
    private Long id;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private String status;
    private String description;
    private String summary;
    private UserDto user; // саме користувач    private String meetingLink;
    private String attachedDocumentUrl;
    private String doctorComment;
}
