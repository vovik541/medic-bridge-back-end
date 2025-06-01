package com.bridge.medic.support.controller;

import com.bridge.medic.support.dto.ApprovalLogDTO;
import com.bridge.medic.support.dto.DoctorReviewRequest;
import com.bridge.medic.support.model.ApprovalLog;
import com.bridge.medic.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BecomeDoctorController {

    private final SupportService supportService;

    @PostMapping("/user/review-request")
    public ResponseEntity<?> createBecomeDoctorRequest(
            @RequestPart("request") DoctorReviewRequest request,
            @RequestPart(value = "attachedReviewDocument", required = false) MultipartFile attachedReviewDocument) {

        supportService.createBecomeDoctorRequest(request, attachedReviewDocument);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/support/review-request")
    public ResponseEntity<List<ApprovalLogDTO>> getAllReviewRequests() {
        List<ApprovalLog> allToReviewRequests = supportService.getAllToReviewRequests();
        List<ApprovalLogDTO> logDtos = new LinkedList<>();
        for (ApprovalLog log : allToReviewRequests) {
            logDtos.add(
                    ApprovalLogDTO.builder()
                            .createdRequestAt(log.getCreatedAt())
                            .documentUrl(log.getDocumentUrl())
                            .doctorType(log.getSpecialistDoctorType().getDoctorType().getName())
                            .firstName(log.getSpecialistDoctorType().getSpecialistData().getUser().getFirstName())
                            .lastName(log.getSpecialistDoctorType().getSpecialistData().getUser().getLastName())
                            .imageUrl(log.getSpecialistDoctorType().getSpecialistData().getUser().getImage_url())
                            .aboutDescription(log.getAboutDoctorComment())
                            .approvalLogId(log.getId())
                            .build()
            );
        }

        return ResponseEntity.ok(logDtos);
    }

    @GetMapping("/support/approve-request")
    public ResponseEntity<?> approveRequest(@RequestParam("logId") Long logId, @RequestParam("reviewComment") String reviewComment) {
        supportService.approveRequest(logId, reviewComment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/support/reject-request")
    public ResponseEntity<?> rejectRequest(@RequestParam("logId") Long logId, @RequestParam("reviewComment") String reviewComment) {
        supportService.rejectRequest(logId, reviewComment);

        return ResponseEntity.ok().build();
    }
}
