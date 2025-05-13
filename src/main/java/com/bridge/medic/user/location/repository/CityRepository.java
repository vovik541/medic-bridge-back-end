package com.bridge.medic.user.location.repository;

import com.bridge.medic.user.location.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {

    Optional<City> findByName(String name);
}