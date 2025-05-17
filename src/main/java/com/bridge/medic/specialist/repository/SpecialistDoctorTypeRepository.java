package com.bridge.medic.specialist.repository;

import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.specialist.model.SpecialistDoctorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SpecialistDoctorTypeRepository extends JpaRepository<SpecialistDoctorType, Integer> {

    Optional<SpecialistDoctorType> findBySpecialistData(SpecialistData specialistData);

    @Query("""
            SELECT sdt FROM SpecialistDoctorType sdt
            WHERE sdt.specialistData.user.id = :specialistId
            """)
    Optional<SpecialistDoctorType> findAllBySpecialistId(Long specialistId); //findAllBySpecialistData_User_Id
}