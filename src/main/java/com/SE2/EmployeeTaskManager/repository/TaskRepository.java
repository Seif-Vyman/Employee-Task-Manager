package com.SE2.EmployeeTaskManager.repository;

import com.SE2.EmployeeTaskManager.entity.Task;
import com.SE2.EmployeeTaskManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(User user);
    List<Task> findByStatus(String status);
    List<Task> findByAssignedToAndStatus(User user, String status);
}