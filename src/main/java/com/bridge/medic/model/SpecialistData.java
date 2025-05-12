package com.bridge.medic.model;

import com.bridge.medic.model.location.City;
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

    @OneToOne
    @JoinColumn(name = "city_id")
    private City city;

    @OneToMany(mappedBy = "specialistData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialistDoctorType> specialistDoctorTypes = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
