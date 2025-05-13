package com.bridge.medic.config.security.authorization.repozitory;

import com.bridge.medic.config.security.authorization.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String roleName);
}