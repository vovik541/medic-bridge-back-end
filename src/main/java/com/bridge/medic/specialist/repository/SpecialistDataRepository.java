package com.bridge.medic.specialist.repository;

import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecialistDataRepository extends JpaRepository<SpecialistData, Integer> {

    Optional<SpecialistData> findByUser(User user);

    List<SpecialistData> findAllByUserId(Long userId);
}