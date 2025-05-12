package com.SE2.Employee.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SE2.Employee.entity.Task;
import com.SE2.Employee.entity.User;
import com.SE2.Employee.service.TaskService;
import com.SE2.Employee.service.UserService;

@RestController
@RequestMapping("/api/employee")
public class EmployeeDashboardController {
    private final TaskService taskService;
    private final UserService userService;

    public EmployeeDashboardController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public List<Task> getTasks(Authentication authentication) {
        String username = authentication.getName();
        User employee = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return taskService.findByUser(employee);
    }

    
}