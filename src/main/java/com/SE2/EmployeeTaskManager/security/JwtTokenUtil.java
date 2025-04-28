package com.SE2.EmployeeTaskManager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.SE2.EmployeeTaskManager.entity.RefreshToken;
import com.SE2.EmployeeTaskManager.service.RefreshTokenService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private String secret = "4234238479ksdfasdf98df912894ehfdsajifh";

    // Access token expiration (1 hour)
    private long accessTokenExpiration = 3600000L;

    private long expiration = 86400000L;
    
    // Refresh token expiration (7 days)
    private long refreshTokenExpiration = 604800000L;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.jwt.accessToken.duration}")
    private long accessTokenDuration;

    public JwtTokenUtil(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }

  

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Existing methods...

    public String generateRefreshToken(UserDetails userDetails) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        return refreshToken.getToken();
    }

    public boolean validateRefreshToken(String token) {
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
            
            refreshTokenService.verifyExpiration(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
} 