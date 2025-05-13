package com.bridge.medic.specialist.model;

import com.bridge.medic.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "specialist_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialistData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specialist_data_id")
    private Long id;

    @OneToMany(mappedBy = "specialistData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialistDoctorType> specialistDoctorTypes = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public void addSpecialistDoctorType(SpecialistDoctorType specialistDoctorType){
        if (specialistDoctorTypes == null){
            specialistDoctorTypes = new ArrayList<>();
        }
        specialistDoctorTypes.add(specialistDoctorType);
        specialistDoctorType.setSpecialistData(this);
    }

    public void removeSpecialistDoctorType(SpecialistDoctorType specialistDoctorType) {
        specialistDoctorTypes.remove(specialistDoctorType);
        specialistDoctorType.setSpecialistData(null);
    }
}
