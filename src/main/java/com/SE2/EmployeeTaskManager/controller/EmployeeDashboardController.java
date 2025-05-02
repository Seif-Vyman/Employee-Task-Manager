package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.service.TaskService;
import com.SE2.EmployeeTaskManager.service.UserService;
import com.SE2.EmployeeTaskManager.entity.Task;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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