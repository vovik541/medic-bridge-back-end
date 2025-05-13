package com.bridge.medic.appointment.model;


import com.bridge.medic.appointment.AppointmentStatus;
import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "specialist_data_id", "start_time"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "specialist_data_id", nullable = false)
    private SpecialistData specialistData;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(name = "summary", length = 255)
    private String summary;
}