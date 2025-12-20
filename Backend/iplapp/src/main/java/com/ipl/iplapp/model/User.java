package com.ipl.iplapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    // total points from correct predictions
    @Column(nullable = false)
    private int points = 0;

    // optional: total predictions, correct predictions
    @Column(nullable = false)
    private int totalPredictions = 0;

    @Column(nullable = false)
    private int correctPredictions = 0;
}
