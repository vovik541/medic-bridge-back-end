package com.bridge.medic.user.model;

import com.bridge.medic.specialist.model.SpecialistData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "specialist_comment",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "specialist_data_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialistComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialist_data_id", nullable = false)
    private SpecialistData specialistData;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private int rating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}