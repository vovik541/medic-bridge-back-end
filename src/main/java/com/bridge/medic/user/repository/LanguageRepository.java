package com.bridge.medic.user.repository;

import com.bridge.medic.user.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Integer> {

    Optional<Language> findByName(String name);
}