package com.shopsphere.shopsphere.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

// @Service — Spring manages this, can be injected anywhere
@Service
public class JwtService {

    // @Value reads from application.properties
    // jwt.secret = the secret key we defined
    // Spring injects the value automatically at startup
    @Value("${jwt.secret}")
    private String secretKey;

    // jwt.expiration = 86400000 (24 hours in milliseconds)
    @Value("${jwt.expiration}")
    private Long expiration;

    // Converts the plain text secret key into a cryptographic key
    // HMAC-SHA256 algorithm needs this format
    // Called internally before signing or validating tokens
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generates a JWT token for a logged-in user
    // email = the subject of the token (who this token belongs to)
    // role = stored inside token so we know permissions without DB call
    public String generateToken(String email, String role) {
        return Jwts.builder()
                // subject = who this token is for
                .subject(email)

                // claim = extra data stored inside the token
                // We store role so we can check permissions without hitting DB
                .claim("role", role)

                // issuedAt = when this token was created
                .issuedAt(new Date())

                // expiration = when this token dies
                // current time + 86400000ms = 24 hours from now
                .expiration(new Date(System.currentTimeMillis() + expiration))

                // signWith = sign the token with our secret key
                // This prevents tampering — if anyone modifies the token,
                // signature verification will fail
                .signWith(getSigningKey())

                // builds and returns the final JWT string
                .compact();
    }

    // Extracts all data (claims) from inside the token
    // Used internally by other methods
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                // set the same key we used to sign
                .verifyWith(getSigningKey())
                .build()
                // parse and verify the token
                // throws exception if token is invalid or tampered
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extracts the email (subject) from the token
    // Used to identify which user is making the request
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extracts the role from the token
    // Used to check if user has permission for an action
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Checks if token has passed its expiration time
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Full token validation
    // Checks: email matches + token not expired
    // Called on every protected API request
    public boolean validateToken(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
    String extractedEmail = extractEmail(token);
    // userDetails.getUsername() = email (set in loadUserByUsername in UserService)
    return extractedEmail.equals(userDetails.getUsername()) && !isTokenExpired(token);
}
}