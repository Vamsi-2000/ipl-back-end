package com.ipl.iplapp.controller;

import com.ipl.iplapp.model.Fixture;
import com.ipl.iplapp.service.FixtureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicFixtureController {

    @Autowired
    private FixtureService fixtureService;

    // GET /api/public/fixtures  → used by users.js
    @GetMapping("/fixtures")
    public List<Fixture> allFixtures() {
        return fixtureService.getAllFixtures();
    }
}
