package com.blackdot.ems.module.dashboard.controller;

import com.blackdot.ems.module.dashboard.service.DashboardService;
import com.blackdot.ems.shared.util.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Get user roles for navigation
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isHR = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_HR"));
        boolean isDataCapturer = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_DATA_CAPTURER"));

        model.addAttribute("user", userDetails);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isHR", isHR);
        model.addAttribute("isDataCapturer", isDataCapturer);

        // Get dashboard statistics
        if (isAdmin || isHR) {
            model.addAttribute("stats", dashboardService.getAdminDashboardStats());
            return "dashboard/admin-dashboard";
        } else if (isDataCapturer) {
            model.addAttribute("assessments", dashboardService.getDataCapturerAssessments(userDetails.getId()));
            return "dashboard/employee/data-capturer-dashboard";
        } else {
            model.addAttribute("profile", dashboardService.getUserProfile(userDetails.getId()));
            return "dashboard/employee/employee-dashboard";
        }
    }

    @GetMapping("/employees")
    public String employeesPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        boolean canManageEmployees = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_HR"));
        
        if (!canManageEmployees) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", userDetails);
        return "dashboard/employee/employees";
    }

    @GetMapping("/assessments")
    public String assessmentsPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        boolean canManageAssessments = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_HR"));
        
        if (!canManageAssessments) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", userDetails);
        return "dashboard/assessment/assessments";
    }

    @GetMapping("/assessments/take")
    public String takeAssessmentPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("user", userDetails);
        return "dashboard/assessment/take-assessment";
    }

    @GetMapping("/reports")
    public String reportsPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        boolean canViewReports = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_HR"));
        
        if (!canViewReports) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", userDetails);
        return "dashboard/report/reports";
    }

    @GetMapping("/tasks")
    public String tasksPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("user", userDetails);
        
        boolean isAdminOrSupervisor = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SUPERVISOR"));
        
        model.addAttribute("canAssignTasks", isAdminOrSupervisor);
        return "dashboard/task/tasks";
    }

    @GetMapping("/audit")
    public String auditPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("user", userDetails);
        return "dashboard/audit/audit";
    }

    @GetMapping("/lifecycle")
    public String lifecyclePage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("user", userDetails);
        return "dashboard/lifecycle/lifecycle";
    }

    @GetMapping("/departments")
    public String departmentsPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        boolean canManageDepartments = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_HR"));
        
        model.addAttribute("user", userDetails);
        model.addAttribute("canManageDepartments", canManageDepartments);
        return "dashboard/department/departments";
    }

    @GetMapping("/roles")
    public String rolesPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        boolean canManageRoles = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        model.addAttribute("user", userDetails);
        model.addAttribute("canManageRoles", canManageRoles);
        return "dashboard/role/roles";
    }
}