package com.SE2.Manager.service;

import com.SE2.Manager.entity.Task;
import com.SE2.Manager.entity.User;
import com.SE2.Manager.repository.TaskRepository;
import com.SE2.Manager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
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
    
    public Task saveTask(Task task) {
        return taskRepository.save(task);
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

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
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

    public List<Task> findByUser(User user) {
        return taskRepository.findByAssignedTo(user);
    }

    public List<Task> findByUserAndStatus(User user, String status) {
        return taskRepository.findByAssignedToAndStatus(user, status);
    }
    @Scheduled(fixedRate = 5000) // every 5 sec
    public void autoInvalidateOverdueTasks() {
        List<Task> tasks = getAllTasks(); // Or taskRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Task task : tasks) {
            if (task.getDeadline() != null
                    && task.getDeadline().isBefore(now)
                    && !"invalid".equalsIgnoreCase(task.getStatus())
                    && !"finished".equalsIgnoreCase(task.getStatus())) {
                task.setStatus("invalid");
                saveTask(task); // or taskRepository.save(task)
            }
        }
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}