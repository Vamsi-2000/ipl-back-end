package com.ipl.iplapp.controller;

import com.ipl.iplapp.model.User;
import com.ipl.iplapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // POST /api/users/register → used by users.js.ensureUserExists
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        Optional<User> existing = userService.findByUsername(user.getUsername());
        User saved = existing.orElseGet(() -> userService.registerUser(user));
        return ResponseEntity.ok(saved);
    }

    // GET /api/users → not used by frontend now, but fine
    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/{username} → used by users.js.ensureUserExists
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        Optional<User> u = userService.findByUsername(username);
        return u.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
