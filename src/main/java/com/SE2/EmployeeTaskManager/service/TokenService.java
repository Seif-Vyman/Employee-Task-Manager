package com.SE2.EmployeeTaskManager.service;

import com.SE2.EmployeeTaskManager.entity.Token;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.repository.TokenRepository;
import com.SE2.EmployeeTaskManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    @Value("${app.jwtRefreshExpirationMs:604800000}") // Default 7 days for refresh token expiration
    private Long refreshTokenDurationMs;

    private final TokenRepository tokenRepository; // Updated to TokenRepository
    private final UserRepository userRepository;

    public TokenService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    // Create both Access and Refresh Tokens
    public Token createTokens(Long userId) {
        Token token = new Token();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        token.setUser(user);
        token.setAccessToken(UUID.randomUUID().toString()); // You could use JWT generation for accessToken here
        token.setRefreshToken(UUID.randomUUID().toString()); // UUID can be replaced with JWT generation for refreshToken
        token.setRefreshTokenExpiry(Instant.now().plusMillis(refreshTokenDurationMs)); // 7 days
        token.setRevoked(false);
        token.setCreatedAt(Instant.now());

        return tokenRepository.save(token);
    }

    public Token saveToken(Token token) {
        return tokenRepository.save(token); // Persist the token to the database
    }

    // Find Token by Refresh Token string
    public Optional<Token> findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken);
    }

    // Verify Refresh Token expiration
    public Token verifyExpiration(Token token) {
        if (token.getRefreshTokenExpiry().isBefore(Instant.now())) {
            tokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please login again.");
        }
        return token;
    }

    // Delete Token by User ID
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        tokenRepository.deleteByUser(user);
    }

    // Optional: Delete the specific refresh token
    public void deleteByRefreshToken(String refreshToken) {
        Optional<Token> tokenOptional = tokenRepository.findByRefreshToken(refreshToken);
        tokenOptional.ifPresent(tokenRepository::delete);
    }

    // Optional: Remove all expired tokens (cleanup)
    public void removeExpiredTokens() {
        tokenRepository.deleteExpiredTokens(Instant.now()); // Call the custom method to delete expired tokens
    }
}
