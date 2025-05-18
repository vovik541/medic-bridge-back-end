package com.bridge.medic.user.controller;

import com.bridge.medic.specialist.model.DoctorType;
import com.bridge.medic.specialist.repository.DoctorTypeRepository;
import com.bridge.medic.user.model.Language;
import com.bridge.medic.user.repository.LanguageRepository;
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
    private final LanguageRepository languageRepository;

    @GetMapping("/specialist-types")
    public ResponseEntity<List<String>> getSpecialistTypes() {
        return ResponseEntity.ok(doctorTypeRepository.findAll().stream().map(DoctorType::getName).toList());
    }
    @GetMapping("/languages-types")
    public ResponseEntity<List<String>> getLanguages() {
        return ResponseEntity.ok(languageRepository.findAll().stream().map(Language::getName).toList());
    }
}
