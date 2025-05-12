package com.bridge.medic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctor_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_type_id")
    private Long id;

    @Column(name = "doctor_type_name")
    private String doctorTypeName;

    @OneToMany(mappedBy = "doctorType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialistDoctorType> specialistDoctorTypes = new ArrayList<>();
}
