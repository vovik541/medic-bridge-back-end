package com.bridge.medic.appointment.repository;

import com.bridge.medic.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.specialistData.user.id = :specialistId
            """)
    List<Appointment> findAllBySpecialistId(@Param("specialistId") Long specialistId);

    List<Appointment> findAllByUser_Id(Long userId);
}