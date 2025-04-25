package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.config.JwtUtil;
import com.SE2.EmployeeTaskManager.dto.AuthResponse;
import com.SE2.EmployeeTaskManager.entity.RefreshToken;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.repository.UserRepository;
import com.SE2.EmployeeTaskManager.service.RefreshTokenService;
import com.SE2.EmployeeTaskManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (authentication.isAuthenticated()) {
                String token = jwtUtil.generateToken(username);
                Optional<User> user = userService.findByUsername(username);
                if (user.isEmpty()) return ResponseEntity.status(404).body("User not found");

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get().getId());

                return ResponseEntity.ok(new AuthResponse(token, refreshToken.getToken()));
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
        Optional<RefreshToken> tokenOptional = refreshTokenService.findByToken(refreshToken);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(403).body("Refresh token not found");
        }

        try {
            RefreshToken token = refreshTokenService.verifyExpiration(tokenOptional.get());
            String accessToken = jwtUtil.generateToken(token.getUser().getUsername());

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(403).body("Refresh token expired. Please login again.");
        }
    }
}
