package com.SE2.Employee.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}