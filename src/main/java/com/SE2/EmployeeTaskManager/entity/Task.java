package com.SE2.EmployeeTaskManager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;


    @Column(nullable = false)
    private String status; // "PENDING", "IN_PROGRESS", "COMPLETED"

    // Many tasks can be assigned to one user
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    // Getters and Setters

}