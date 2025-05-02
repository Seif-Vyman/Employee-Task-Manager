package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.service.UserService;
import com.SE2.EmployeeTaskManager.service.TaskService;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/manager")
public class ManagerDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        return ResponseEntity.ok(new Object[]{
                userService.getEmployees(),
                taskService.getAllTasks()
        });
    }

    @PostMapping("/tasks/add")
    public ResponseEntity<?> addTask(
            @RequestParam("employee_id") Long employeeId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("due_date") String deadline
    ) {
        User employee = userService.getUserById(employeeId);
        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee not found");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(LocalDateTime.parse(deadline));
        task.setAssignedTo(employee);
        task.setStatus("pending");
        taskService.saveTask(task);

        return ResponseEntity.ok("Task assigned successfully");
    }
}