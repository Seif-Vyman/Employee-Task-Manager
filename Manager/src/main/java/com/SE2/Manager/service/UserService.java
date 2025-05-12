package com.SE2.Manager.service;

import com.SE2.Manager.entity.User;
import com.SE2.Manager.repository.UserRepository;
import com.SE2.Manager.dto.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(RegisterRequest request) {
        // 1. Check if username or email exists
        if (userRepository.existsByUsername(request.getUsername()) || 
            userRepository.existsByEmail(request.getEmail())) {
            return null; // In use, return null for error
        }

        // 2. Map request → entity, hash password, set role
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // If User has a "roles" collection, adjust accordingly
        try {
            user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Handle bad input gracefully, e.g. return error to user
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        // 3. Save the user
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public List<User> getEmployees() {
        return userRepository.findByRole(User.Role.EMPLOYEE);  // CORRECT: Passing the enum value
    }

}