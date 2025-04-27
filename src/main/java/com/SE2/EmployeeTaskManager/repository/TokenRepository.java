package com.SE2.EmployeeTaskManager.repository;

import com.SE2.EmployeeTaskManager.entity.Token;
import com.SE2.EmployeeTaskManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    // Find token by refreshToken string
    Optional<Token> findByRefreshToken(String refreshToken);

    // Find token by accessToken string
    Optional<Token> findByAccessToken(String accessToken);

    // Find active token by user
    Optional<Token> findByUserAndRevokedFalse(User user);

    // Optional: list all tokens for a user (in case you support multiple sessions)
    List<Token> findAllByUser(User user);

    // Delete all tokens for a user (e.g. on account deletion)
    void deleteByUser(User user);

    // Custom query to delete expired tokens based on refreshTokenExpiry date
    @Query("DELETE FROM Token t WHERE t.refreshTokenExpiry < :currentTime")
    void deleteExpiredTokens(Instant currentTime);
}
