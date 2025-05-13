package com.bridge.medic.user.location.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "country")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "iso_code", unique = true, length = 10)
    private String isoCode;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Region> regions = new ArrayList<>();
}