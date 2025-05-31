package com.bridge.medic.specialist.service;

import com.bridge.medic.appointment.repository.AppointmentRepository;
import com.bridge.medic.specialist.repository.SpecialistDataRepository;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SpecialistService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SpecialistDataRepository specialistDataRepository;
    private final AppointmentRepository appointmentRepository;

    public List<User> findSpecialists(String city, String language, String specialistType) {
        if (Objects.isNull(city) || city.isEmpty()) {
            return userRepository.searchSpecialistsByLanguageAndType(language, specialistType);
        }

        return userRepository.searchSpecialistsByCityLanguageAndType(city, language, specialistType);
    }


    public List<String> findAllApprovedPositionsBySpecialistId(Long specialistId) {
        return userRepository.findDoctorTypeNamesByUserId(specialistId);
    }

    public boolean isSpecialist(Long userId) {
        return !specialistDataRepository.findAllByUserId(userId).isEmpty();
    }
}
