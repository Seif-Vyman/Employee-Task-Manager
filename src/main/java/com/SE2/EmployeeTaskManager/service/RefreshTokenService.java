package com.SE2.EmployeeTaskManager.service;

import com.SE2.EmployeeTaskManager.entity.RefreshToken;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.repository.RefreshTokenRepository;
import com.SE2.EmployeeTaskManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.refreshToken.duration}")
    private long refreshTokenDurationMs;

    public RefreshTokenService(
        RefreshTokenRepository refreshTokenRepository, 
        UserRepository userRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String username) {
        // Find the user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete existing refresh tokens for the user
        refreshTokenRepository.deleteByUser(user);

        // Create new refresh token
        RefreshToken refreshToken = new RefreshToken(
            UUID.randomUUID().toString(), 
            user, 
            Instant.now().plusMillis(refreshTokenDurationMs)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new login request");
        }
        return token;
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}