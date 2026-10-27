package com.shopsphere.shopsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// This is what your API SENDS BACK to the frontend after login or register
// Contains the JWT token the frontend will store and send with every request
@Data
@AllArgsConstructor
public class AuthResponse {

    // The JWT token — frontend stores this in localStorage
    // Every subsequent API call sends this in the Authorization header
    private String token;

    // We also send back basic user info so frontend can display it
    // Example: show "Welcome, Preethi" on the dashboard
    private String fullName;
    private String email;
    private String role;
}