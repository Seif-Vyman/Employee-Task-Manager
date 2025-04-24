package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.entity.Task;
import com.SE2.EmployeeTaskManager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestParam Long assignedToUserId) {
        Task createdTask = taskService.createTask(task, assignedToUserId);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        Task updatedTask = taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
}