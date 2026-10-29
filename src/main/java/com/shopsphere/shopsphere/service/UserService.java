
package com.shopsphere.shopsphere.service;

import com.shopsphere.shopsphere.dto.RegisterRequest;
import com.shopsphere.shopsphere.dto.AuthResponse;
import com.shopsphere.shopsphere.dto.LoginRequest;
import com.shopsphere.shopsphere.model.User;
import com.shopsphere.shopsphere.repository.UserRepository;
import com.shopsphere.shopsphere.security.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // JwtService injected — Spring finds it because of @Service on JwtService
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Build user object
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .role(request.getRole() != null ? request.getRole() : "BUYER")
                .isActive(true)
                .build();

        // Save to database
        User savedUser = userRepository.save(user);

        // Generate REAL JWT token now — not placeholder
        // Token contains email and role
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole());

        return new AuthResponse(token, savedUser.getFullName(), savedUser.getEmail(), savedUser.getRole());
    }

    public AuthResponse login(LoginRequest request) {

        // Step 1 — Find user by email
        // orElseThrow = if user not found, throw exception immediately
        // This stops execution — no user = no login
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2 — Check if account is active
        // Deactivated accounts cannot login
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Step 3 — Verify password
        // passwordEncoder.matches() hashes the incoming plain password
        // and compares it to the stored hash
        // We never decrypt — bcrypt only goes one way
        // "preethi123" → hash → compare with stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // Step 4 — Generate JWT token
        // Token contains email and role — valid for 24 hours
        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        // Step 5 — Return token + user info
        return new AuthResponse(token, user.getFullName(), user.getEmail(), user.getRole());
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPasswordHash())
            .roles(user.getRole())
            .build();
    }
}