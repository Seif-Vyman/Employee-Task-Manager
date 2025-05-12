package com.SE2.Manager.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role; // Or List<String> roles, if you support multiple
}