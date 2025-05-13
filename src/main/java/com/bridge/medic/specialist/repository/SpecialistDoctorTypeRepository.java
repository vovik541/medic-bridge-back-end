package com.bridge.medic.specialist.repository;

import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.specialist.model.SpecialistDoctorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialistDoctorTypeRepository extends JpaRepository<SpecialistDoctorType, Integer> {

    Optional<SpecialistDoctorType> findBySpecialistData(SpecialistData specialistData);
}