package com.blackdot.ems.module.authentication.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('DATA_CAPTURER') or hasRole('SUPERVISOR') or hasRole('HR') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/supervisor")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('HR') or hasRole('ADMIN')")
    public String supervisorAccess() {
        return "Supervisor Board.";
    }
    
    @GetMapping("/hr")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public String hrAccess() {
        return "HR Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
    
    @GetMapping("/datacapturer")
    @PreAuthorize("hasRole('DATA_CAPTURER')")
    public String dataCapturerAccess() {
        return "Data Capturer Board.";
    }
    
    @GetMapping("/employee-api")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public String employeeApiAccess() {
        return "Employee API Access - You can manage employees!";
    }
}