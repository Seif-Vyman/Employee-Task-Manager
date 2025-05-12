package com.SE2.Employee.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private User assignedTo;


    @Column(name = "deadline", nullable = true)
    private LocalDateTime deadline;

    // Getters and Setters

}