package com.SE2.EmployeeTaskManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.SE2.EmployeeTaskManager.service.CustomUserDetailsService;
import com.SE2.EmployeeTaskManager.security.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler successHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.successHandler = successHandler;
    }

    // Password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Dao Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // @Bean
    // public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
    //     return new CustomAuthenticationSuccessHandler();
    // }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disabling CSRF for simplicity, customize if needed
            .authorizeRequests()
                .requestMatchers("/auth/**", "/users/register", "/html/**").permitAll() // Allow register and login pages
                .requestMatchers("/api/tasks/user/{userId}").authenticated()
                .anyRequest().authenticated() // Ensure all other requests are authenticated
            .and()
            .formLogin()
                .loginPage("/html/login.html") // Custom login page
                .loginProcessingUrl("/auth/login") // Endpoint to handle login POST
                .successHandler(successHandler) // Redirect to this page upon successful login
                .failureUrl("/html/login.html?error=true") // Redirect back to login page on failure with error message
                .permitAll();
            // .and()
            // .logout()
            //     .logoutUrl("/auth/logout") // URL to log out the user
            //     .logoutSuccessUrl("/html/login.html?logout") // Redirect after successful logout
            //     .permitAll();

        return http.build();
    }
}
