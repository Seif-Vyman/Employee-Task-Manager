package com.SE2.EmployeeTaskManager.service;

import com.SE2.EmployeeTaskManager.entity.Task;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.repository.TaskRepository;
import com.SE2.EmployeeTaskManager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(Task task, Long assignedToUserId) {
        Optional<User> userOpt = userRepository.findById(assignedToUserId);
        if (userOpt.isPresent()) {
            task.setAssignedTo(userOpt.get());
            task.setStatus("PENDING");
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Assigned user not found");
        }
    }

    public List<Task> getTasksByUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return taskRepository.findByAssignedTo(userOpt.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    public Task updateTaskStatus(Long taskId, String status) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(status);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}