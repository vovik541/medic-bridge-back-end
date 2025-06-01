package com.bridge.medic.user.repository;

import com.bridge.medic.user.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {

    Optional<Region> findByName(String name);
}