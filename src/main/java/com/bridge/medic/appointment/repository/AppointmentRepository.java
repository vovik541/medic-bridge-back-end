package com.bridge.medic.appointment.repository;

import com.bridge.medic.appointment.AppointmentStatus;
import com.bridge.medic.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.specialistData.user.id = :specialistId
            """)
    List<Appointment> findAllBySpecialistId(@Param("specialistId") Long specialistId);

    List<Appointment> findAllByUser_Id(Long userId);

    @Query("""
                SELECT a FROM Appointment a
                WHERE a.specialistData.user.id = :specialistId
                    AND a.status = 'PENDING'
            """)
    List<Appointment> findPendingAppointmentsBySpecialistId(@Param("specialistId") Long specialistId);

    @Query("""
                SELECT a FROM Appointment a
                WHERE a.specialistData.user.id = :specialistId
                    AND a.status = :status
            """)
    List<Appointment> findAppointmentsBySpecialistIdAndStatus(@Param("specialistId") Long specialistId,
                                                              @Param("status") AppointmentStatus status);

    @Query("""
                SELECT a FROM Appointment a
                WHERE a.specialistData.user.id = :specialistId
                    AND a.status = :status
                    AND a.endTime > :now
            """)
    List<Appointment> findAppointmentsBySpecialistIdAndStatusAfterTime(@Param("specialistId") Long specialistId,
                                                                       @Param("status") AppointmentStatus status,
                                                                       @Param("now") OffsetDateTime now);

    @Query("""
                SELECT a FROM Appointment a
                WHERE a.specialistData.user.id = :specialistId
                    AND a.endTime < :now
                    AND a.status NOT IN (:excludedStatuses)
            """)
    List<Appointment> findAppointmentsBySpecialistIdAndBeforeTimeAndStatusNotIn(@Param("specialistId") Long specialistId,
                                                                                @Param("now") OffsetDateTime now,
                                                                                @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses);

    @Query("""
                SELECT a FROM Appointment a
                WHERE a.specialistData.user.id = :specialistId
                    AND a.status != :status
                    AND a.endTime < :now
            """)
    List<Appointment> findAppointmentsBySpecialistIdAndNotStatusBeforeTime(@Param("specialistId") Long specialistId,
                                                                           @Param("status") AppointmentStatus status,
                                                                           @Param("now") OffsetDateTime now);
    @Query("""
                SELECT a FROM Appointment a
                WHERE a.specialistData.user.id = :specialistId
                    AND a.status = :status
                    AND a.endTime < :now
            """)
    List<Appointment> findAppointmentsBySpecialistIdAndStatusBeforeTime(@Param("specialistId") Long specialistId,
                                                                           @Param("status") AppointmentStatus status,
                                                                           @Param("now") OffsetDateTime now);
}