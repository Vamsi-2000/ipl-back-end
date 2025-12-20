package com.ipl.iplapp.service;

import com.ipl.iplapp.model.Prediction;
import com.ipl.iplapp.model.User;
import com.ipl.iplapp.repostitory.PredictionRepository;
import com.ipl.iplapp.repostitory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private UserRepository userRepository;

    // Save single prediction; ensure user exists and increment counters
    public Prediction savePrediction(Prediction prediction) {
        if (prediction.getUsername() == null || prediction.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Prediction must contain username");
        }
        String username = prediction.getUsername().trim();

        // Create or fetch user
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User nu = User.builder()
                    .username(username)
                    .points(0)
                    .totalPredictions(0)
                    .correctPredictions(0)
                    .build();
            return userRepository.save(nu);
        });

        // initialize prediction state
        prediction.setResult("Pending");
        prediction.setPoints(0);
        prediction.setCorrect(false);

        Prediction saved = predictionRepository.save(prediction);

        // increment user's total predictions
        user.setTotalPredictions(user.getTotalPredictions() + 1);
        userRepository.save(user);

        return saved;
    }

    public List<Prediction> saveAllPredictions(List<Prediction> predictions) {
        List<Prediction> out = new ArrayList<>();
        for (Prediction p : predictions) {
            out.add(savePrediction(p));
        }
        return out;
    }

    public List<Prediction> getPredictionsByUser(String username) {
        return predictionRepository.findByUsername(username);
    }

    public List<Prediction> getPredictionsByFixture(Long fixtureId) {
        return predictionRepository.findByFixtureId(fixtureId);
    }

    public List<Prediction> getAllPredictions() {
        return predictionRepository.findAll();
    }

    // When admin announces winner for a fixture -> update each prediction's correctness & points
    public List<Prediction> updatePredictionsAfterResult(Long fixtureId, String winner) {
        List<Prediction> predictions = predictionRepository.findByFixtureId(fixtureId);
        List<Prediction> updated = new ArrayList<>();

        for (Prediction p : predictions) {
            boolean correct = p.getPredictedWinner() != null &&
                    p.getPredictedWinner().equalsIgnoreCase(winner);

            p.setCorrect(correct);
            p.setPoints(correct ? 2 : 0);
            p.setResult(correct ? "Correct" : "Wrong");
            updated.add(p);

            // update user's aggregate counters
            userRepository.findByUsername(p.getUsername()).ifPresent(u -> {
                if (correct) {
                    u.setPoints(u.getPoints() + 2);
                    u.setCorrectPredictions(u.getCorrectPredictions() + 1);
                }
                userRepository.save(u);
            });
        }
        return predictionRepository.saveAll(updated);
    }

    // Leaderboard computed from users
    public List<Map<String, Object>> getLeaderboard() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .sorted((a, b) -> Integer.compare(b.getPoints(), a.getPoints()))
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("username", u.getUsername());
                    map.put("points", u.getPoints());
                    map.put("totalPredictions", u.getTotalPredictions());
                    map.put("correctPredictions", u.getCorrectPredictions());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // -------- Admin operations --------
    public Prediction updatePredictionAdmin(Long id, Prediction incoming) {
        return predictionRepository.findById(id)
                .map(p -> {
                    if (incoming.getPredictedWinner() != null) {
                        p.setPredictedWinner(incoming.getPredictedWinner());
                    }
                    p.setPoints(incoming.getPoints());
                    p.setResult(incoming.getResult());
                    p.setCorrect(incoming.isCorrect());
                    return predictionRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Prediction not found"));
    }

    public void deletePrediction(Long id) {
        // 1. Find prediction
        Prediction p = predictionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prediction not found"));

        String username = p.getUsername();
        if (username != null && !username.trim().isEmpty()) {
            username = username.trim();

            // 2. Delete ALL predictions of this user
            List<Prediction> userPreds = predictionRepository.findByUsername(username);
            predictionRepository.deleteAll(userPreds);

            // 3. Delete the user row itself
            userRepository.findByUsername(username)
                    .ifPresent(userRepository::delete);
        } else {
            // Fallback: if somehow no username, just delete that prediction
            predictionRepository.delete(p);
        }
    }

}
