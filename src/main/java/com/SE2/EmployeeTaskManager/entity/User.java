package com.SE2.EmployeeTaskManager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.Email;
import java.util.List;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid email address")
    private String email;

    @Column(nullable = false)
    private String password; // Store hashed password

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // "MANAGER" or "EMPLOYEE"

    // One user can have many tasks assigned
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
    private List<Task> tasks;

    public enum Role {
        MANAGER, EMPLOYEE;
    }
}
