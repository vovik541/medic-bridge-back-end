package com.bridge.medic.support.service;

import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.specialist.model.DoctorType;
import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.specialist.model.SpecialistDoctorType;
import com.bridge.medic.specialist.repository.DoctorTypeRepository;
import com.bridge.medic.specialist.repository.SpecialistDataRepository;
import com.bridge.medic.specialist.repository.SpecialistDoctorTypeRepository;
import com.bridge.medic.storage.service.FileLocalStorageService;
import com.bridge.medic.support.ApprovalStatus;
import com.bridge.medic.support.dto.DoctorReviewRequest;
import com.bridge.medic.support.model.ApprovalLog;
import com.bridge.medic.support.repository.ApprovalLogRepository;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final AuthenticatedUserService authenticatedUserService;
    private final DoctorTypeRepository doctorTypeRepository;
    private final ApprovalLogRepository approvalLogRepository;
    private final SpecialistDoctorTypeRepository specialistDoctorTypeRepository;
    private final SpecialistDataRepository specialistDataRepository;
    private final LanguageRepository languageRepository;

    private final FileLocalStorageService fileLocalStorageService;

    @Transactional
    public void createBecomeDoctorRequest(DoctorReviewRequest request, MultipartFile file) {
        User user = authenticatedUserService.getCurrentUser();
        user.addLanguageIfAbsent(languageRepository.findByName(request.getAdditionalLanguage()).orElseThrow());

        ApprovalLog approvalLog = new ApprovalLog();
        SpecialistDoctorType specialistDoctorType = new SpecialistDoctorType();

        SpecialistData specialistData = user.getSpecialistData();
        if (specialistData == null) {
            specialistData = new SpecialistData();
            specialistData.setUser(user);
        }

        specialistData.addSpecialistDoctorType(specialistDoctorType);
        specialistDoctorType.setSpecialistData(specialistData);

        //TODO check if doctor type exists for this log
        DoctorType doctorType = doctorTypeRepository.findByName(request.getDoctorType()).orElseThrow();
        specialistDoctorType.setDoctorType(doctorType);
        specialistDoctorType.setApproved(false);

        approvalLog.setCreatedAt(LocalDateTime.now());
        approvalLog.setStatus(ApprovalStatus.PENDING);

        String filePath = fileLocalStorageService.storeFile(file, "to_review/");
        approvalLog.setDocumentUrl(filePath);
        approvalLog.setAboutDoctorComment(request.getAboutMeDescription());
        approvalLog.setSpecialistDoctorType(specialistDoctorType);

        specialistDoctorTypeRepository.save(specialistDoctorType);
        specialistDataRepository.save(specialistData);
        approvalLogRepository.save(approvalLog);
    }


}
