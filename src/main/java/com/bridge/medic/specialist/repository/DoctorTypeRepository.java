package com.bridge.medic.specialist.repository;

import com.bridge.medic.specialist.model.DoctorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorTypeRepository extends JpaRepository<DoctorType, Integer> {

    Optional<DoctorType> findByName(String name);
}