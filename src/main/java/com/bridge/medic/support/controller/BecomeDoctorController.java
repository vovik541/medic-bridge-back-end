package com.bridge.medic.support.controller;

import com.bridge.medic.support.dto.DoctorReviewRequest;
import com.bridge.medic.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class BecomeDoctorController {

    private final SupportService supportService;

    @PostMapping("/review-request")
    public ResponseEntity<?> createBecomeDoctorRequest(
            @RequestPart("request") DoctorReviewRequest request,
            @RequestPart(value = "attachedReviewDocument", required = false) MultipartFile attachedReviewDocument) {

        supportService.createBecomeDoctorRequest(request, attachedReviewDocument);

        return ResponseEntity.ok().build();
    }

}
