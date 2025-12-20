package com.ipl.iplapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixtures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fixture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamA;
    private String teamB;

    private LocalDateTime matchTime;
    private LocalDateTime deadline;

    private String matchType;

    private String winner;
}
