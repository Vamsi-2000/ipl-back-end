package com.ipl.iplapp.controller;

import com.ipl.iplapp.model.Prediction;
import com.ipl.iplapp.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/predictions")
@CrossOrigin("*")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    // POST /api/predictions → submit prediction (users.js)
    @PostMapping
    public Prediction submitPrediction(@RequestBody Prediction prediction) {
        return predictionService.savePrediction(prediction);
    }

    @PostMapping("/bulk")
    public List<Prediction> submitBulk(@RequestBody List<Prediction> predictions) {
        return predictionService.saveAllPredictions(predictions);
    }

    // GET /api/predictions/user/{username} → for user specific (users.js)
    @GetMapping("/user/{username}")
    public List<Prediction> getByUser(@PathVariable String username) {
        return predictionService.getPredictionsByUser(username);
    }

    // GET /api/predictions/fixture/{fixtureId}
    @GetMapping("/fixture/{fixtureId}")
    public List<Prediction> getByFixture(@PathVariable Long fixtureId) {
        return predictionService.getPredictionsByFixture(fixtureId);
    }

    // GET /api/predictions → used by index.js & admin.js
    @GetMapping
    public List<Prediction> getAll() {
        return predictionService.getAllPredictions();
    }

    // POST /api/predictions/updateResult/{fixtureId}?winner=CSK
    @PostMapping("/updateResult/{fixtureId}")
    public List<Prediction> updateResults(@PathVariable Long fixtureId,
                                          @RequestParam String winner) {
        return predictionService.updatePredictionsAfterResult(fixtureId, winner);
    }

    // GET /api/predictions/leaderboard → used by index.js/users.js
    @GetMapping("/leaderboard")
    public List<Map<String, Object>> leaderboard() {
        return predictionService.getLeaderboard();
    }
}
