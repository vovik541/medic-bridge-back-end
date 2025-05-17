package com.bridge.medic.specialist.service;

import com.bridge.medic.specialist.repository.SpecialistDataRepository;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialistService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SpecialistDataRepository specialistDataRepository;

    public List<User> findSpecialists(String city, String language, String specialistType) {
        return userRepository.searchSpecialists(city, language, specialistType);
    }

//    public User findSpecialistBySpecialistDataId(long id) {
//        return specialistDataRepository.findSpecialistByDataId(id);
//    }

    public List<String> findAllApprovedPositionsBySpecialistId(Long specialistId) {
        return userRepository.findDoctorTypeNamesByUserId(specialistId);
    }

    public boolean isSpecialist(Long userId) {
        return !specialistDataRepository.findAllByUserId(userId).isEmpty();
    }
}
