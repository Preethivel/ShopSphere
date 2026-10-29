package com.shopsphere.shopsphere.security;
// placed in security package — all auth related code lives here

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// these are standard Java web objects
// HttpServletRequest = incoming request
// HttpServletResponse = outgoing response
// FilterChain = chain of filters, we call it to pass request to next filter or controller

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// represents an authenticated user in Spring Security
// once we set this, Spring knows the user is authenticated

import org.springframework.security.core.context.SecurityContextHolder;
// SecurityContext holds the current authenticated user for this request
// every request has its own SecurityContext

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
// UserDetailsService = loads user from DB by email
// UserDetails = Spring's representation of a user

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// adds extra details like IP address to the authentication object

import org.springframework.stereotype.Component;
// marks this as a Spring managed bean

import org.springframework.web.filter.OncePerRequestFilter;
// guarantees this filter runs ONCE per request — not multiple times

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
// extends OncePerRequestFilter = runs exactly once per HTTP request

    private final JwtService jwtService;
    // to extract email and validate token

    private final UserDetailsService userDetailsService;
    // to load user from DB using email extracted from token

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
    // doFilterInternal = the actual filter logic
    // called automatically by Spring for every request

        String authHeader = request.getHeader("Authorization");
        // read the Authorization header from request
        // format: "Bearer eyJhbGci..."

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            // no token found = pass request through without authentication
            // SecurityConfig will block if route needs auth
            return;
        }

        String token = authHeader.substring(7);
        // remove "Bearer " (7 characters) to get the actual token
        // "Bearer eyJhbGci..." → "eyJhbGci..."

        String email = jwtService.extractEmail(token);
        // decode the token and get the email from payload

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // email found in token AND user not already authenticated
            // second check prevents processing token twice

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            // load user from DB using email
            // if user doesn't exist, throws UsernameNotFoundException

            if (jwtService.isTokenValid(token, userDetails)) {
                // check token is not expired and belongs to this user

                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // create authentication object
                // userDetails = who the user is
                // null = no credentials needed (already verified via token)
                // getAuthorities() = user's roles like BUYER, SELLER

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // attach request details like IP address to auth object

                SecurityContextHolder.getContext().setAuthentication(authToken);
                // tell Spring Security this user is authenticated for this request
                // all subsequent checks in this request will see this user as authenticated
            }
        }

        filterChain.doFilter(request, response);
        // pass request to the next filter or controller
    }
}