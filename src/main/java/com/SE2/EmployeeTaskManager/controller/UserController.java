package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.service.UserService;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }


}