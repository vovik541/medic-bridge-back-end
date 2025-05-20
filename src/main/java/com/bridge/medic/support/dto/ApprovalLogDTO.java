package com.bridge.medic.support.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApprovalLogDTO {

    private Long approvalLogId;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String doctorType;
    private String documentUrl;
    private LocalDateTime createdRequestAt;
    private String aboutDescription;
}
