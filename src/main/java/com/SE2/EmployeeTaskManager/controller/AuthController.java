package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.config.JwtUtil;
import com.SE2.EmployeeTaskManager.dto.AuthResponse;
import com.SE2.EmployeeTaskManager.entity.Token;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.repository.UserRepository;
import com.SE2.EmployeeTaskManager.service.TokenService;
import com.SE2.EmployeeTaskManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService; // updated name from RefreshTokenService
    private final UserRepository userRepository;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtUtil jwtUtil,
            TokenService tokenService,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (authentication.isAuthenticated()) {
                Optional<User> userOptional = userService.findByUsername(username);
                if (userOptional.isEmpty()) {
                    return ResponseEntity.status(404).body("User not found");
                }

                User user = userOptional.get();

                // Generate tokens
                String accessToken = jwtUtil.generateToken(username);
                String refreshTokenStr = jwtUtil.generateRefreshToken(username);

                // Save to DB
                Token token = new Token();
                token.setUser(user);
                token.setAccessToken(accessToken);
                token.setRefreshToken(refreshTokenStr);
                token.setRefreshTokenExpiry(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days
                token.setRevoked(false);
                token.setCreatedAt(Instant.now());

                tokenService.saveToken(token);

                // Return both tokens in response
                return ResponseEntity.ok(new AuthResponse(accessToken, refreshTokenStr));
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Login failed: " + ex.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        if(savedUser == null){
            return ResponseEntity.badRequest().body("Username or Email already exists");
        }
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        Optional<Token> tokenOptional = tokenService.findByRefreshToken(refreshToken);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(403).body("Refresh token not found");
        }

        try {
            Token token = tokenService.verifyExpiration(tokenOptional.get());

            String newAccessToken = jwtUtil.generateToken(token.getUser().getUsername());

            // Optional: update access token in DB if you want to track latest one
            token.setAccessToken(newAccessToken);
            tokenService.saveToken(token);

            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(403).body("Refresh token expired. Please login again.");
        }
    }
}
