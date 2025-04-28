package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.dto.AuthResponse;
import com.SE2.EmployeeTaskManager.dto.LoginRequest;
import com.SE2.EmployeeTaskManager.dto.RefreshTokenRequest;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.security.JwtTokenUtil;
import com.SE2.EmployeeTaskManager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, 
                          UserService userService, 
                          JwtTokenUtil jwtTokenUtil,
                          UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Authenticate the user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            )
        );

        // Load user details
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        
        // Generate access and refresh tokens
        final String accessToken = jwtTokenUtil.generateToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        // Return tokens
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            // Validate the refresh token
            String refreshToken = refreshTokenRequest.getRefreshToken();
            String username = jwtTokenUtil.extractUsername(refreshToken);

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate refresh token
            if (jwtTokenUtil.validateRefreshToken(refreshToken, userDetails)) {
                // Generate new access token
                String newAccessToken = jwtTokenUtil.generateToken(userDetails);

                // Optionally generate a new refresh token
                String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

                return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
            }

            return ResponseEntity.badRequest().body("Invalid refresh token");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error refreshing token");
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
}