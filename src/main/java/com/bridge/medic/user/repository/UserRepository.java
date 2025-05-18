package com.bridge.medic.user.repository;

import com.bridge.medic.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u WHERE u.email = :emailOrLogin OR u.login = :emailOrLogin")
    Optional<User> findByEmailOrLogin(@Param("emailOrLogin") String emailOrLogin);

    @Query("""
            SELECT DISTINCT u FROM User u
            JOIN u.specialistData sd
            JOIN sd.specialistDoctorTypes sdt
            JOIN sdt.doctorType dt
            LEFT JOIN u.languages l
            LEFT JOIN u.city c
            WHERE sdt.approved = true
              AND (:city IS NULL OR c.name = :city)
              AND (:language IS NULL OR l.name = :language)
              AND (:specialization IS NULL OR dt.name = :specialization)
            """)
    List<User> searchSpecialistsByCityLanguageAndType(
            @Param("city") String city,
            @Param("language") String language,
            @Param("specialization") String specialization
    );
    @Query("""
            SELECT DISTINCT u FROM User u
            JOIN u.specialistData sd
            JOIN sd.specialistDoctorTypes sdt
            JOIN sdt.doctorType dt
            LEFT JOIN u.languages l
            WHERE sdt.approved = true
              AND (:language IS NULL OR l.name = :language)
              AND (:specialization IS NULL OR dt.name = :specialization)
            """)
    List<User> searchSpecialistsByLanguageAndType(
            @Param("language") String language,
            @Param("specialization") String specialization
    );
    @Query("""
                SELECT DISTINCT dt.name
                FROM User u
                JOIN u.specialistData sd
                JOIN sd.specialistDoctorTypes sdt
                JOIN sdt.doctorType dt
                WHERE u.id = :userId
            """)
    List<String> findDoctorTypeNamesByUserId(@Param("userId") Long userId);
}
