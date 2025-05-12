package com.SE2.Manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.SE2.Manager.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                // Use default session management without maximumSessions
                .invalidSessionUrl("/login.html"))
            .authorizeHttpRequests(authz -> authz
                // Static resources and HTML files
                .requestMatchers(
                    "/", 
                    "/admin.html", 
                    "/employee.html", 
                    "/login.html", 
                    "/register.html", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/favicon.ico")
                .permitAll()
                // Auth endpoints are public
                .requestMatchers("/api/manager/auth/**").permitAll()
                // This is the critical part - match the exact role text from your login response
                .requestMatchers("/api/manager/**").hasRole("MANAGER")
                .requestMatchers("/api/employee/**").hasRole("EMPLOYEE")
                // Other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .securityContext(securityContext -> securityContext
                .securityContextRepository(securityContextRepository())
                .requireExplicitSave(false));
        
        return http.build();
    }
}