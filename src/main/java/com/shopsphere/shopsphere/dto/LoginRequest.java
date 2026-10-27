package com.shopsphere.shopsphere.dto;

import lombok.Data;

// What the frontend sends when user fills the login form
// Only email and password — nothing else needed for login
@Data
public class LoginRequest {

    private String email;
    private String password;
}