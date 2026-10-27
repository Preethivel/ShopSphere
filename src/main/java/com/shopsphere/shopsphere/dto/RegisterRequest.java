package com.shopsphere.shopsphere.dto;

import lombok.Data;

// DTO = Data Transfer Object
// This is what the frontend SENDS to your API when registering
// It is NOT the User entity — it's just the incoming request shape
// We never expose the User entity directly to the outside world

// Why separate DTO from Entity?
// Entity has passwordHash, isActive, createdAt — frontend should never send those
// DTO has only what the user fills in the registration form
@Data
public class RegisterRequest {

    // Full name from the registration form
    private String fullName;

    // Email from the registration form
    private String email;

    // Plain text password — we receive it here, hash it in service, never store plain
    private String password;

    // BUYER or SELLER — user picks this during registration
    // ADMIN accounts are created separately, never through public registration
    private String role;
}