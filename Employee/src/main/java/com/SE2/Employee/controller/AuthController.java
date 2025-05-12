package com.SE2.Employee.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SE2.Employee.dto.LoginRequest;
import com.SE2.Employee.dto.RegisterRequest;
import com.SE2.Employee.entity.User;
import com.SE2.Employee.service.UserService;
@RestController
@RequestMapping("/api/employee/auth/")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                // Get roles from the authentication object
                var authorities = authentication.getAuthorities()
                        .stream()
                        .map(grantedAuth -> grantedAuth.getAuthority())
                        .collect(Collectors.toList());

                        
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "roles", authorities));
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Login failed: " + ex.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        User savedUser = userService.registerUser(registerRequest);
        if (savedUser == null) {
            return ResponseEntity.badRequest().body("Username or Email already exists");
        }
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}
