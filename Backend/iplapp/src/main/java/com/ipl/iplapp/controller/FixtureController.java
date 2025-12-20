package com.ipl.iplapp.controller;

import com.ipl.iplapp.model.Fixture;
import com.ipl.iplapp.service.FixtureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fixtures")
@CrossOrigin(origins = "*")
public class FixtureController {

    @Autowired
    private FixtureService fixtureService;

    // GET /api/admin/fixtures  → used by admin.js and index.js
    @GetMapping
    public List<Fixture> getAllFixtures() {
        return fixtureService.getAllFixtures();
    }

    // POST /api/admin/fixtures  → add fixture (admin.js)
    @PostMapping
    public Fixture addFixture(@RequestBody Fixture fixture) {
        return fixtureService.addFixture(fixture);
    }

    // PUT /api/admin/fixtures/{id} → update fixture (if needed)
    @PutMapping("/{id}")
    public Fixture updateFixture(@PathVariable Long id, @RequestBody Fixture fixture) {
        return fixtureService.updateFixture(id, fixture);
    }

    // DELETE /api/admin/fixtures/{id} → delete fixture (if needed)
    @DeleteMapping("/{id}")
    public void deleteFixture(@PathVariable Long id) {
        fixtureService.deleteFixture(id);
    }

    // PUT /api/admin/fixtures/{id}/winner?winner=CSK → used by admin.js
    @PutMapping("/{id}/winner")
    public Fixture announceWinner(@PathVariable Long id,
                                  @RequestParam String winner) {
        return fixtureService.announceWinner(id, winner);
    }
}
