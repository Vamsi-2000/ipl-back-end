package com.ipl.iplapp.repostitory;

import com.ipl.iplapp.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    List<Prediction> findByUsername(String username);

    List<Prediction> findByFixtureId(Long fixtureId);

    void deleteByFixtureId(Long fixtureId);
}
