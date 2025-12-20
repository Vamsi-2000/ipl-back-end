package com.ipl.iplapp.controller;

import com.ipl.iplapp.model.Prediction;
import com.ipl.iplapp.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/predictions")
@CrossOrigin(origins = "*")
public class AdminPredictionController {

    @Autowired
    private PredictionService predictionService;

    // PUT /api/admin/predictions/{id} → used by admin.js (savePrediction)
    @PutMapping("/{id}")
    public ResponseEntity<Prediction> updatePrediction(@PathVariable Long id,
                                                       @RequestBody Prediction incoming) {
        Prediction updated = predictionService.updatePredictionAdmin(id, incoming);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/admin/predictions/{id} → used by admin.js (deletePrediction)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrediction(@PathVariable Long id) {
        predictionService.deletePrediction(id);
        return ResponseEntity.noContent().build();
    }



}
