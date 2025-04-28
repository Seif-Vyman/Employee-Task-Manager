package com.SE2.EmployeeTaskManager.service;

import com.SE2.EmployeeTaskManager.config.JwtUtil;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.repository.UserRepository;
import com.SE2.EmployeeTaskManager.util.JasyptUtil;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            // Username exists
            return null;
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            // Email exists
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(JasyptUtil.encrypt(user.getEmail()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
        .map(user -> {
            user.setEmail(JasyptUtil.decrypt(user.getEmail()));
            return user;
        });
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
        .map(user -> {
            user.setEmail(JasyptUtil.decrypt(user.getEmail()));
            return user;
        });
    }

}