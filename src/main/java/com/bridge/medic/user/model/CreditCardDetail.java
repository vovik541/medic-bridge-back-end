package com.bridge.medic.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Month;
import java.time.Year;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_card_detail")
public class CreditCardDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "credit_card_detail_id")
    private Long id;
    @Column(nullable = false)
    private Integer number;
    @Column(nullable = false)
    private Month month;
    @Column(nullable = false)
    private Year year;
    @Column(nullable = false, name = "first_name")
    private String firstName;
    @Column(nullable = false, name = "last_name")
    private String lastName;
}