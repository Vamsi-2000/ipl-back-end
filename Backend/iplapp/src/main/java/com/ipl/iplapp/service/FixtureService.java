package com.ipl.iplapp.service;

import com.ipl.iplapp.model.Fixture;
import com.ipl.iplapp.model.Prediction;
import com.ipl.iplapp.model.User;
import com.ipl.iplapp.repostitory.FixtureRepository;
import com.ipl.iplapp.repostitory.PredictionRepository;
import com.ipl.iplapp.repostitory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FixtureService {

    @Autowired
    private FixtureRepository fixtureRepository;

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private UserRepository userRepository;


    public Fixture addFixture(Fixture fixture) {
        return fixtureRepository.save(fixture);
    }

    public List<Fixture> getAllFixtures() {
        return fixtureRepository.findAll();
    }

    public Fixture updateFixture(Long id, Fixture fixtureDetails) {
        return fixtureRepository.findById(id)
                .map(fixture -> {
                    fixture.setTeamA(fixtureDetails.getTeamA());
                    fixture.setTeamB(fixtureDetails.getTeamB());
                    fixture.setMatchTime(fixtureDetails.getMatchTime());
                    fixture.setDeadline(fixtureDetails.getDeadline());
                    fixture.setMatchType(fixtureDetails.getMatchType());
                    return fixtureRepository.save(fixture);
                })
                .orElseThrow(() -> new RuntimeException("Fixture not found"));
    }

    /**
     * Delete fixture:
     *  - rollback user stats based on predictions of this fixture
     *  - delete predictions of this fixture
     *  - delete fixture
     *  - then delete ALL users that have 0 points, 0 totalPredictions, 0 correctPredictions
     */
    public void deleteFixture(Long id) {
        // get all predictions for this fixture
        List<Prediction> preds = predictionRepository.findByFixtureId(id);

        // rollback users based on those predictions
        for (Prediction p : preds) {
            String username = p.getUsername();
            if (username == null || username.trim().isEmpty()) continue;

            userRepository.findByUsername(username.trim()).ifPresent(u -> {
                // reduce total predictions
                u.setTotalPredictions(Math.max(0, u.getTotalPredictions() - 1));

                // rollback points if this prediction was correct and had points
                if (p.isCorrect() && p.getPoints() > 0) {
                    u.setPoints(Math.max(0, u.getPoints() - p.getPoints()));
                    u.setCorrectPredictions(Math.max(0, u.getCorrectPredictions() - 1));
                }

                userRepository.save(u);
            });
        }

        // delete all predictions of this fixture
        predictionRepository.deleteByFixtureId(id);

        // delete fixture
        fixtureRepository.deleteById(id);

        // GLOBAL CLEANUP: delete all users with zero stats
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (u.getPoints() == 0 &&
                    u.getTotalPredictions() == 0 &&
                    u.getCorrectPredictions() == 0) {
                userRepository.delete(u);
            }
        }
    }

    // ----------------- Winner logic -----------------

    // Announce winner and update predictions + users
    public Fixture announceWinner(Long id, String winner) {

        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fixture not found"));

        fixture.setWinner(winner);
        fixtureRepository.save(fixture);

        List<Prediction> predictions = predictionRepository.findByFixtureId(id);

        for (Prediction p : predictions) {
            boolean correct = p.getPredictedWinner() != null &&
                    p.getPredictedWinner().equalsIgnoreCase(winner);
            p.setCorrect(correct);
            p.setPoints(correct ? 2 : 0);
            p.setResult(correct ? "Correct" : "Wrong");
            predictionRepository.save(p);

            // award user points if correct
            userRepository.findByUsername(p.getUsername()).ifPresent(user -> {
                if (correct) {
                    user.setPoints(user.getPoints() + 2);
                    user.setCorrectPredictions(user.getCorrectPredictions() + 1);
                }
                userRepository.save(user);
            });
        }
        return fixture;
    }

    // optional bulk-add
    public List<Fixture> addAllFix(List<Fixture> fixtures) {
        return fixtureRepository.saveAll(fixtures);
    }
}
