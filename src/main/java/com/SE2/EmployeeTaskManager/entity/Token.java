package com.SE2.EmployeeTaskManager.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the user
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Refresh token
    @Column(nullable = false, unique = true, length = 1000)
    private String refreshToken;

    // Access token (optional, but useful for tracking)
    @Column(nullable = false, unique = true, length = 1000)
    private String accessToken;

    // Expiry for refresh token (usually long-lived)
    @Column(nullable = false)
    private Instant refreshTokenExpiry;

    // Flag to mark if token has been revoked (for logout, etc.)
    @Column(nullable = false)
    private boolean revoked = false;

    // Optional: time token was created
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Instant getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(Instant refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
