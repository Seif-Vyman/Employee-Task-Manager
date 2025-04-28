package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.service.TaskService;
import com.SE2.EmployeeTaskManager.service.UserService;
import com.SE2.EmployeeTaskManager.entity.Task;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
public class EmployeeDashboardController {
    private final TaskService taskService;
    private final UserService userService;

    public EmployeeDashboardController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/employee/dashboard")
    public String getDashboard(
            Model model,
            Authentication authentication,
            @RequestParam(value = "filterByStatus", required = false, defaultValue = "false") boolean filterByStatus) {

        String username = authentication.getName();
        User employee = userService.findByUsername(username).orElse(null);
        if (employee == null) {
            return "redirect:/html/login.html";
        }
        List<Task> tasks = taskService.getTasksByUser(employee.getId());

        LocalDateTime now = LocalDateTime.now();
        // ---- AUTO-UPDATE INVALID TASKS ----
        for (Task task : tasks) {
            if (task.getDeadline() != null
                    && task.getDeadline().isBefore(now)
                    && !"invalid".equalsIgnoreCase(task.getStatus())
                    && !"finished".equalsIgnoreCase(task.getStatus())) {
                task.setStatus("invalid");
                taskService.saveTask(task);
            }
        }
        // ---- END AUTO-UPDATE ----

        if (filterByStatus) {
            // Sort: pending → in progress → finished → invalid (deadline passed)
            tasks.sort(Comparator.comparingInt(t -> getStatusOrder(t, now)));
        } else {
            // Sort by deadline (soonest first), nulls last
            tasks.sort(Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder())));
        }

        model.addAttribute("filterByStatus", filterByStatus);
        model.addAttribute("tasks", tasks);
        model.addAttribute("employee", employee);
        model.addAttribute("now", now);
        return "employee";
    }

    @PostMapping("/tasks/updateStatus/{taskId}")
    public String updateTaskStatus(
            @PathVariable Long taskId,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "filterByStatus", required = false, defaultValue = "false") boolean filterByStatus) {
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            redirectAttributes.addFlashAttribute("error", "Task not found.");
            return "redirect:/employee/dashboard" + (filterByStatus ? "?filterByStatus=true" : "");
        }

        if (task.getDeadline() != null && task.getDeadline().isBefore(LocalDateTime.now())) {
            // Optional: update DB status to invalid if you want to persist this
            if (!"invalid".equalsIgnoreCase(task.getStatus())
                    && !"finished".equalsIgnoreCase(task.getStatus())) {
                task.setStatus("invalid");
                taskService.saveTask(task);
            }
            redirectAttributes.addFlashAttribute("error", "Deadline expired. Task cannot be updated.");
            return "redirect:/employee/dashboard" + (filterByStatus ? "?filterByStatus=true" : "");
        }

        // Update logic (pending → in progress, in progress → finished)
        String currentStatus = task.getStatus();
        if ("pending".equalsIgnoreCase(currentStatus)) {
            task.setStatus("in progress");
        } else if ("in progress".equalsIgnoreCase(currentStatus)) {
            task.setStatus("finished");
        }

        taskService.saveTask(task);
        return "redirect:/employee/dashboard" + (filterByStatus ? "?filterByStatus=true" : "");
    }

    private int getStatusOrder(Task t, LocalDateTime now) {
        if (t.getDeadline() != null && t.getDeadline().isBefore(now))
            return 3; // Invalid
        String s = t.getStatus().toLowerCase();
        if (s.equals("pending"))
            return 0;
        if (s.equals("in progress"))
            return 1;
        if (s.equals("finished"))
            return 2;
        return 3;
    }
}