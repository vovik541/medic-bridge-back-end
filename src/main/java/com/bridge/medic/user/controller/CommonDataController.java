package com.bridge.medic.user.controller;

import com.bridge.medic.specialist.model.DoctorType;
import com.bridge.medic.specialist.repository.DoctorTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/commons")
@RequiredArgsConstructor
public class CommonDataController {

    private final DoctorTypeRepository doctorTypeRepository;

    @GetMapping("/specialist-types")
    public ResponseEntity<List<String>> getCurrentUserInfo() {
        return ResponseEntity.ok(doctorTypeRepository.findAll().stream().map(DoctorType::getName).toList());
    }
}
