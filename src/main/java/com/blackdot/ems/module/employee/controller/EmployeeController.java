package com.blackdot.ems.module.employee.controller;

import com.blackdot.ems.module.employee.dto.*;
import com.blackdot.ems.module.employee.service.EmployeeService;
import com.blackdot.ems.shared.dto.MessageResponse;
import com.blackdot.ems.shared.entity.EmployeeStatus;
import com.blackdot.ems.shared.entity.EmploymentType;
import com.blackdot.ems.shared.entity.ClearanceLevel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        List<EmployeeResponse> results;
        
        // Start with appropriate base dataset
        if (search != null && !search.trim().isEmpty()) {
            // If there's a search term, start with search results
            results = employeeService.searchEmployees(search);
        } else {
            // Otherwise start with all employees
            results = employeeService.getAllEmployees();
        }
        
        // Apply status filter if provided (works on search results too)
        if (status != null && !status.trim().isEmpty()) {
            try {
                EmployeeStatus employeeStatus = EmployeeStatus.valueOf(status);
                results = results.stream()
                        .filter(emp -> emp.getEmployeeStatus() == employeeStatus)
                        .collect(java.util.stream.Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
            }
        }
        
        // Apply role filter if provided (works on search + status filtered results)
        if (role != null && !role.trim().isEmpty()) {
            final String roleToMatch = role.toUpperCase();
            results = results.stream()
                    .filter(emp -> emp.getRoleNames() != null && 
                            emp.getRoleNames().stream()
                                    .anyMatch(r -> r.equalsIgnoreCase(roleToMatch)))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Convert to Page manually for consistency
        int start = page * size;
        int end = Math.min((start + size), results.size());
        List<EmployeeResponse> pageContent = results.isEmpty() ? results : results.subList(start, end);
        Page<EmployeeResponse> resultPage = new org.springframework.data.domain.PageImpl<>(
            pageContent, 
            org.springframework.data.domain.PageRequest.of(page, size), 
            results.size()
        );
        return ResponseEntity.ok(resultPage);
    }
    
    @GetMapping("/list")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployeesList() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployeesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<EmployeeResponse> employees = employeeService.getAllEmployeesPaginated(page, size, sortBy, sortDir);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeResponse>> getActiveEmployees() {
        List<EmployeeResponse> employees = employeeService.getActiveEmployees();
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByRole(@PathVariable String roleName) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByRole(roleName);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(@RequestParam String q) {
        List<EmployeeResponse> employees = employeeService.searchEmployees(q);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }
    
    @GetMapping("/employee-id/{employeeId}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        EmployeeResponse employee = employeeService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(employee);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, 
                                                          @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeResponse employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(employee);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new MessageResponse("Employee deactivated successfully!"));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> activateEmployee(@PathVariable Long id) {
        employeeService.activateEmployee(id);
        return ResponseEntity.ok(new MessageResponse("Employee activated successfully!"));
    }
    
    @PutMapping("/{id}/change-password")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> changePassword(@PathVariable Long id, 
                                                         @Valid @RequestBody ChangePasswordRequest request) {
        employeeService.changePassword(id, request);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }
    
    // ========== Government-Grade Employee Management Endpoints ==========
    
    /**
     * Change employee status with audit trail
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> changeEmployeeStatus(
            @PathVariable Long id,
            @RequestParam EmployeeStatus status,
            @RequestParam String reason,
            @RequestParam Long changedBy) {
        EmployeeResponse employee = employeeService.changeEmployeeStatus(id, status, reason, changedBy);
        return ResponseEntity.ok(employee);
    }
    
    /**
     * Get employees by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByStatus(@PathVariable EmployeeStatus status) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Update employee clearance level
     */
    @PutMapping("/{id}/clearance")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> updateClearanceLevel(
            @PathVariable Long id,
            @RequestParam ClearanceLevel level,
            @RequestParam String backgroundCheckStatus,
            @RequestParam String checkDate) {
        java.time.LocalDate date = java.time.LocalDate.parse(checkDate);
        EmployeeResponse employee = employeeService.updateSecurityLevel(id, level, backgroundCheckStatus, date);
        return ResponseEntity.ok(employee);
    }
    
    /**
     * Get employees requiring background check renewal
     */
    @GetMapping("/compliance/background-check-renewal")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesRequiringBackgroundCheckRenewal() {
        List<EmployeeResponse> employees = employeeService.getEmployeesRequiringBackgroundCheckRenewal();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees with expiring contracts
     */
    @GetMapping("/compliance/expiring-contracts")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesWithExpiringContracts() {
        List<EmployeeResponse> employees = employeeService.getEmployeesWithExpiringContracts();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees in probation
     */
    @GetMapping("/compliance/probation")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesInProbation() {
        List<EmployeeResponse> employees = employeeService.getEmployeesInProbation();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get comprehensive employee statistics
     */
    @GetMapping("/government-stats")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeService.GovernmentGradeStats> getEmployeeStatistics() {
        EmployeeService.GovernmentGradeStats stats = employeeService.getGovernmentGradeStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get all employee statuses for dropdown
     */
    @GetMapping("/enums/statuses")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getEmployeeStatuses() {
        Map<String, Object> statuses = new HashMap<>();
        for (EmployeeStatus status : EmployeeStatus.values()) {
            Map<String, Object> statusInfo = new HashMap<>();
            statusInfo.put("name", status.name());
            statusInfo.put("displayName", status.getDisplayName());
            statusInfo.put("description", status.getDescription());
            statusInfo.put("hasSystemAccess", status.hasSystemAccess());
            statusInfo.put("isPayrollEligible", status.isPayrollEligible());
            statuses.put(status.name(), statusInfo);
        }
        return ResponseEntity.ok(statuses);
    }
    
    /**
     * Get all employment types for dropdown
     */
    @GetMapping("/enums/employment-types")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getEmploymentTypes() {
        Map<String, Object> types = new HashMap<>();
        for (EmploymentType type : EmploymentType.values()) {
            Map<String, Object> typeInfo = new HashMap<>();
            typeInfo.put("name", type.name());
            typeInfo.put("displayName", type.getDisplayName());
            typeInfo.put("description", type.getDescription());
            typeInfo.put("standardProbationMonths", type.getStandardProbationMonths());
            typeInfo.put("eligibleForBenefits", type.isEligibleForBenefits());
            typeInfo.put("requiresFormalContract", type.requiresFormalContract());
            types.put(type.name(), typeInfo);
        }
        return ResponseEntity.ok(types);
    }
    
    /**
     * Get all security levels for dropdown
     */
    @GetMapping("/enums/clearance-levels")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getSecurityLevels() {
        Map<String, Object> levels = new HashMap<>();
        for (ClearanceLevel level : ClearanceLevel.values()) {
            Map<String, Object> levelInfo = new HashMap<>();
            levelInfo.put("name", level.name());
            levelInfo.put("displayName", level.getDisplayName());
            levelInfo.put("description", level.getDescription());
            levelInfo.put("hierarchyLevel", level.getHierarchyLevel());
            levelInfo.put("reviewPeriodMonths", level.getReviewPeriodMonths());
            levelInfo.put("requiresBackgroundCheck", level.requiresBackgroundCheck());
            levels.put(level.name(), levelInfo);
        }
        return ResponseEntity.ok(levels);
    }
    
    @GetMapping("/statistics/detailed")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getDetailedEmployeeStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Legacy statistics for backward compatibility
        stats.put("totalEmployees", employeeService.getTotalEmployeeCount());
        stats.put("activeEmployees", employeeService.getActiveEmployeeCount());
        stats.put("admins", employeeService.getEmployeeCountByRole("ADMIN"));
        stats.put("hrStaff", employeeService.getEmployeeCountByRole("HR"));
        stats.put("dataCapturers", employeeService.getEmployeeCountByRole("DATA_CAPTURER"));
        stats.put("supervisors", employeeService.getEmployeeCountByRole("SUPERVISOR"));
        stats.put("employees", employeeService.getEmployeeCountByRole("EMPLOYEE"));
        
        // Government-grade statistics
        EmployeeService.GovernmentGradeStats governmentStats = employeeService.getGovernmentGradeStats();
        stats.put("governmentGradeStats", governmentStats);
        
        // Quick compliance alerts
        stats.put("employeesRequiringBackgroundCheckRenewal", governmentStats.getEmployeesRequiringBackgroundCheckRenewal());
        stats.put("employeesWithExpiringContracts", governmentStats.getEmployeesWithExpiringContracts());
        stats.put("employeesInProbation", governmentStats.getEmployeesInProbation());
        
        return ResponseEntity.ok(stats);
    }
}