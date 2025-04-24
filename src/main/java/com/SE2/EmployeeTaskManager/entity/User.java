package com.SE2.EmployeeTaskManager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Store hashed password

    @Column(nullable = false)
    private String role; // "MANAGER" or "EMPLOYEE"

    // One user can have many tasks assigned
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
    private List<Task> tasks;

    // Getters and Setters

}