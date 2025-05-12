package com.bridge.medic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "specialist_doctor_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialistDoctorType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialist_data_id")
    private SpecialistData specialistData;

    @ManyToOne
    @JoinColumn(name = "doctor_type_id")
    private DoctorType doctorType;

    @Column(name = "is_approved")
    private boolean approved;
}