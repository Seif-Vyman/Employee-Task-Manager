package com.SE2.EmployeeTaskManager.controller;

import com.SE2.EmployeeTaskManager.service.UserService;
import com.SE2.EmployeeTaskManager.service.TaskService;
import com.SE2.EmployeeTaskManager.entity.User;
import com.SE2.EmployeeTaskManager.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ManagerDashboardController {

    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;

    @GetMapping("/manager/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("employees", userService.getEmployees());
        model.addAttribute("tasks", taskService.getAllTasks()); // or leave out if not needed

        return "admin"; // Make sure this matches your HTML Thymeleaf filename
    }

}