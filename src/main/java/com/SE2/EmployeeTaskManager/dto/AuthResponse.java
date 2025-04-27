package com.SE2.EmployeeTaskManager.dto;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    public AuthResponse() {}

    // Constructor for initializing the AuthResponse with tokens
    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getter for accessToken
    public String getAccessToken() {
        return accessToken;
    }

    // Setter for accessToken
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter for refreshToken
    public String getRefreshToken() {
        return refreshToken;
    }

    // Setter for refreshToken
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Optional: Override toString() for better logging and response output (for debugging)
    //@Override
    //public String toString() {
    //    return "AuthResponse{" +
    //            "accessToken='" + accessToken + '\'' +
    //            ", refreshToken='" + refreshToken + '\'' +
    //            '}';
    //}
}
