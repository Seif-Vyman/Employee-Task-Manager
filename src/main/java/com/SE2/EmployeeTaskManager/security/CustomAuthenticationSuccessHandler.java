package com.SE2.EmployeeTaskManager.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        
        // System.out.println("User roles: " + roles);
        if (roles.contains("ROLE_MANAGER")) {
            response.sendRedirect("/manager/dashboard");
        } else if (roles.contains("ROLE_EMPLOYEE")) {
            response.sendRedirect("/employee/dashboard");
        } else {
            response.sendRedirect("/html/login.html");
        }
    }
}