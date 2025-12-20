package com.ipl.iplapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Long fixtureId;
    private String predictedWinner;

    private boolean correct = false;  // true if prediction matches result
    private int points = 0;           // default 0
    private String result = "Pending"; // Pending / Correct / Wrong
}
