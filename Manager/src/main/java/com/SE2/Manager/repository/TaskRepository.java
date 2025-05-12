package com.SE2.Manager.repository;

import com.SE2.Manager.entity.Task;
import com.SE2.Manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(User user);
    List<Task> findByStatus(String status);
    List<Task> findByAssignedToAndStatus(User user, String status);
}