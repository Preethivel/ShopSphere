package com.shopsphere.shopsphere.controller;

import com.shopsphere.shopsphere.dto.AuthResponse;
import com.shopsphere.shopsphere.dto.LoginRequest;
import com.shopsphere.shopsphere.dto.RegisterRequest;
import com.shopsphere.shopsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // POST http://localhost:8080/api/auth/login
    // User sends email + password
    // We verify and return JWT token
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);

            // 200 OK = standard response for successful login
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            // Wrong email or password = 401 Unauthorized
            // Never tell user which one was wrong — security best practice
            // "Invalid credentials" not "wrong password" or "email not found"
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}