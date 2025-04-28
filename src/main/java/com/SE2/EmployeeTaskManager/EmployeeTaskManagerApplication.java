package com.SE2.EmployeeTaskManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmployeeTaskManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeTaskManagerApplication.class, args);
		System.out.println("Employee Task Manager Application is running");
	}

}
