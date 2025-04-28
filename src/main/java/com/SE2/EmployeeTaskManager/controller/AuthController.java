package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.dto.AuthResponse;
import com.SE2.EmployeeTaskManager.dto.LoginRequest;
import com.SE2.EmployeeTaskManager.dto.RefreshTokenRequest;
import com.SE2.EmployeeTaskManager.entity.RefreshToken;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.security.JwtTokenUtil;
import com.SE2.EmployeeTaskManager.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, 
                          UserService userService, 
                          JwtTokenUtil jwtTokenUtil,
                          UserDetailsService userDetailsService,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
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
        
        // Generate access token
        final String accessToken = jwtTokenUtil.generateToken(userDetails);
        
        // Generate and store refresh token
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        // Return tokens
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            // Validate refresh token
            String refreshToken = refreshTokenRequest.getRefreshToken();
            
            if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
                return ResponseEntity.badRequest().body("Invalid or expired refresh token");
            }

            // Find the refresh token in the database
            RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUser().getUsername());

            // Generate new access token
            String newAccessToken = jwtTokenUtil.generateToken(userDetails);

            // Generate new refresh token
            String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error refreshing token: " + e.getMessage());
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