package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.service.UserService;
import com.SE2.EmployeeTaskManager.service.TaskService;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class ManagerDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/manager/dashboard")
    public String dashboard(Model model) {
        // Get all employees (users with 'employee' role)
        model.addAttribute("employees", userService.getEmployees());
        model.addAttribute("tasks", taskService.getAllTasks());
        return "admin"; // This should match your Thymeleaf template name (e.g., admin.html)
    }

    @PostMapping("/tasks/add")
    public String addTask(
            @RequestParam("employee_id") Long employeeId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("due_date") String deadline,
            Model model
    ) {
        User employee = userService.getUserById(employeeId);
        if (employee == null) {
            model.addAttribute("error", "Employee not found!");
            model.addAttribute("employees", userService.getEmployees());
            model.addAttribute("tasks", taskService.getAllTasks());
            return "admin"; // Or "dashboard" if your Thymeleaf file is named dashboard.html
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(LocalDateTime.parse(deadline));
        task.setAssignedTo(employee);
        task.setStatus("pending");
        taskService.saveTask(task);

        model.addAttribute("message", "Task assigned successfully!");
        model.addAttribute("employees", userService.getEmployees());
        model.addAttribute("tasks", taskService.getAllTasks());
        return "admin"; // Or "dashboard" if your Thymeleaf file is named dashboard.html
    }
}