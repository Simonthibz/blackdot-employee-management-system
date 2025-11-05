package com.blackdot.ems.module.employee.service;

import com.blackdot.ems.shared.entity.User;
import com.blackdot.ems.shared.entity.Role;
import com.blackdot.ems.shared.entity.ERole;
import com.blackdot.ems.shared.entity.EmployeeStatus;
import com.blackdot.ems.shared.entity.EmploymentType;
import com.blackdot.ems.shared.entity.ClearanceLevel;
import com.blackdot.ems.module.employee.dto.*;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.module.authentication.repository.RoleRepository;
import com.blackdot.ems.module.department.repository.DepartmentRepository;
import com.blackdot.ems.shared.entity.Department;
import com.blackdot.ems.shared.exception.ResourceNotFoundException;
import com.blackdot.ems.shared.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<EmployeeResponse> getAllEmployees() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public Page<EmployeeResponse> getAllEmployeesPaginated(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToResponse);
    }
    
    public List<EmployeeResponse> getActiveEmployees() {
        // Get employees with active statuses for government-grade system
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getEmployeeStatus() != null && user.getEmployeeStatus().hasSystemAccess())
                .collect(Collectors.toList());
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<EmployeeResponse> getEmployeesByRole(String roleName) {
        try {
            // If the roleName already has ROLE_ prefix, use it as is, otherwise add the prefix
            String roleEnumName = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName.toUpperCase();
            ERole eRole = ERole.valueOf(roleEnumName);
            List<User> users = userRepository.findActiveUsersByRole(eRole);
            return users.stream().map(this::convertToResponse).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + roleName);
        }
    }
    
    public List<EmployeeResponse> searchEmployees(String searchTerm) {
        List<User> users = userRepository.searchActiveUsers(searchTerm);
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public EmployeeResponse getEmployeeById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return convertToResponse(user);
    }
    
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return convertToResponse(user);
    }
    
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        // Validate uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        
        if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException("Employee ID is already in use!");
        }
        
        // Create user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName()
        );
        
        // Basic employee information
        user.setEmployeeId(request.getEmployeeId());
        
        // Set department entity relationship
        if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
            Department department = departmentRepository.findByName(request.getDepartment())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + request.getDepartment()));
            user.setDepartmentEntity(department);
        }
        
        user.setPosition(request.getPosition());
        user.setHireDate(request.getHireDate() != null ? request.getHireDate() : LocalDate.now());
        
        // Government-Grade Status with defaults or request values
        user.setEmployeeStatus(request.getEmployeeStatus() != null ? request.getEmployeeStatus() : EmployeeStatus.PENDING_START);
        user.setEmploymentType(request.getEmploymentType() != null ? request.getEmploymentType() : EmploymentType.PERMANENT);
        user.setClearanceLevel(request.getClearanceLevel() != null ? request.getClearanceLevel() : ClearanceLevel.PUBLIC);
        
        // Organizational Information
        user.setBranchOffice(request.getBranchOffice());
        user.setCostCenter(request.getCostCenter());
        user.setJobGrade(request.getJobGrade());
        
        // Contract Information with defaults or request values
        user.setContractStartDate(request.getContractStartDate() != null ? request.getContractStartDate() : user.getHireDate());
        user.setContractEndDate(request.getContractEndDate());
        user.setProbationEndDate(request.getProbationEndDate() != null ? request.getProbationEndDate() : user.getHireDate().plusMonths(6));
        user.setNoticePeriodDays(request.getNoticePeriodDays() != null ? request.getNoticePeriodDays() : 30);
        
        // Contact Information
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmergencyContactName(request.getEmergencyContactName());
        user.setAddress(request.getAddress());
        
        // Professional Information
        user.setHighestQualification(request.getHighestQualification());
        user.setYearsOfExperience(request.getYearsOfExperience());
        user.setProfessionalCertifications(request.getProfessionalCertifications());
        
        // Security & Compliance with defaults or request values
        user.setBackgroundCheckStatus(request.getBackgroundCheckStatus() != null ? request.getBackgroundCheckStatus() : "Pending");
        user.setBackgroundCheckDate(request.getBackgroundCheckDate());
        user.setSecurityTrainingCompleted(request.getSecurityTrainingCompleted() != null ? request.getSecurityTrainingCompleted() : false);
        user.setConfidentialityAgreementSigned(request.getConfidentialityAgreementSigned() != null ? request.getConfidentialityAgreementSigned() : false);
        user.setDataPrivacyConsent(request.getDataPrivacyConsent() != null ? request.getDataPrivacyConsent() : false);
        
        // Set roles
        Set<String> strRoles = request.getRoles();
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null || strRoles.isEmpty()) {
            Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(employeeRole);
        } else {
            strRoles.forEach(role -> {
                try {
                    ERole eRole = ERole.valueOf("ROLE_" + role.toUpperCase());
                    Role userRole = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found."));
                    roles.add(userRole);
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid role: " + role);
                }
            });
        }
        
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Check if email is being changed and if it's already in use
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        
        // Check if employee ID is being changed and if it's already in use
        if (!user.getEmployeeId().equals(request.getEmployeeId()) && 
            userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException("Employee ID is already in use!");
        }
        
        // Update basic fields
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmployeeId(request.getEmployeeId());
        
        // Update department entity relationship
        if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
            Department department = departmentRepository.findByName(request.getDepartment())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + request.getDepartment()));
            user.setDepartmentEntity(department);
        }
        
        user.setPosition(request.getPosition());
        user.setHireDate(request.getHireDate());
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Government-Grade Status
        if (request.getEmployeeStatus() != null) {
            user.setEmployeeStatus(request.getEmployeeStatus());
        }
        if (request.getEmploymentType() != null) {
            user.setEmploymentType(request.getEmploymentType());
        }
        if (request.getClearanceLevel() != null) {
            user.setClearanceLevel(request.getClearanceLevel());
        }
        
        // Organizational Information
        user.setBranchOffice(request.getBranchOffice());
        user.setCostCenter(request.getCostCenter());
        user.setJobGrade(request.getJobGrade());
        
        // Contract Information
        user.setContractStartDate(request.getContractStartDate());
        user.setContractEndDate(request.getContractEndDate());
        user.setProbationEndDate(request.getProbationEndDate());
        if (request.getNoticePeriodDays() != null) {
            user.setNoticePeriodDays(request.getNoticePeriodDays());
        }
        
        // Contact Information
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmergencyContactName(request.getEmergencyContactName());
        user.setAddress(request.getAddress());
        
        // Professional Information
        user.setHighestQualification(request.getHighestQualification());
        user.setYearsOfExperience(request.getYearsOfExperience());
        user.setProfessionalCertifications(request.getProfessionalCertifications());
        
        // Security & Compliance
        user.setBackgroundCheckStatus(request.getBackgroundCheckStatus());
        user.setBackgroundCheckDate(request.getBackgroundCheckDate());
        if (request.getSecurityTrainingCompleted() != null) {
            user.setSecurityTrainingCompleted(request.getSecurityTrainingCompleted());
        }
        if (request.getConfidentialityAgreementSigned() != null) {
            user.setConfidentialityAgreementSigned(request.getConfidentialityAgreementSigned());
        }
        if (request.getDataPrivacyConsent() != null) {
            user.setDataPrivacyConsent(request.getDataPrivacyConsent());
        }
        
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Update roles if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            request.getRoles().forEach(role -> {
                try {
                    ERole eRole = ERole.valueOf("ROLE_" + role.toUpperCase());
                    Role userRole = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found."));
                    roles.add(userRole);
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid role: " + role);
                }
            });
            user.setRoles(roles);
        }
        
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    
    public void deleteEmployee(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Government-grade soft delete with audit trail
        user.setEmployeeStatus(EmployeeStatus.TERMINATED);
        user.setIsActive(false);
        user.setStatusChangeReason("Employee terminated via system");
        user.setStatusChangeDate(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void activateEmployee(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Government-grade activation with audit trail
        user.setEmployeeStatus(EmployeeStatus.ACTIVE);
        user.setIsActive(true);
        user.setStatusChangeReason("Employee activated via system");
        user.setStatusChangeDate(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect!");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    public long getTotalEmployeeCount() {
        return userRepository.count();
    }
    
    public long getActiveEmployeeCount() {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmployeeStatus() != null && user.getEmployeeStatus().hasSystemAccess())
                .count();
    }
    
    public long getEmployeeCountByRole(String roleName) {
        try {
            ERole eRole = ERole.valueOf("ROLE_" + roleName.toUpperCase());
            return userRepository.findActiveUsersByRole(eRole).size();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + roleName);
        }
    }
    
    private EmployeeResponse convertToResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name().replace("ROLE_", ""))
                .collect(Collectors.toSet());
        
        EmployeeResponse response = new EmployeeResponse();
        
        // Basic Information
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmployeeId(user.getEmployeeId());
        
        // Set department from entity relationship
        if (user.getDepartmentEntity() != null) {
            response.setDepartment(user.getDepartmentEntity().getName());
        }
        
        response.setPosition(user.getPosition());
        response.setHireDate(user.getHireDate());
        
        // Government-Grade Status Management
        // Handle legacy users without employeeStatus
        EmployeeStatus effectiveStatus = user.getEmployeeStatus();
        if (effectiveStatus == null) {
            // Derive status from isActive for backward compatibility
            effectiveStatus = Boolean.TRUE.equals(user.getIsActive()) 
                ? EmployeeStatus.ACTIVE 
                : EmployeeStatus.INACTIVE;
        }
        response.setEmployeeStatus(effectiveStatus);
        response.setEmploymentType(user.getEmploymentType());
        response.setClearanceLevel(user.getClearanceLevel());
        response.setBranchOffice(user.getBranchOffice());
        response.setCostCenter(user.getCostCenter());
        response.setReportingManagerId(user.getReportingManagerId());
        response.setJobGrade(user.getJobGrade());
        response.setSalaryBand(user.getSalaryBand());
        
        // Set reporting manager name if available
        if (user.getReportingManagerId() != null) {
            userRepository.findById(user.getReportingManagerId())
                    .ifPresent(manager -> response.setReportingManagerName(manager.getFullName()));
        }
        
        // Contract Information
        response.setContractStartDate(user.getContractStartDate());
        response.setContractEndDate(user.getContractEndDate());
        response.setProbationEndDate(user.getProbationEndDate());
        response.setNoticePeriodDays(user.getNoticePeriodDays());
        response.setInProbation(user.isInProbation());
        response.setContractExpiringSoon(user.isContractExpiringSoon());
        
        // Contact Information
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmergencyContactName(user.getEmergencyContactName());
        response.setEmergencyContactPhone(user.getEmergencyContactPhone());
        response.setAddress(user.getAddress());
        
        // Professional Information
        response.setHighestQualification(user.getHighestQualification());
        response.setProfessionalCertifications(user.getProfessionalCertifications());
        response.setLanguagesSpoken(user.getLanguagesSpoken());
        response.setPreviousEmployer(user.getPreviousEmployer());
        response.setYearsOfExperience(user.getYearsOfExperience());
        
        // Security & Compliance
        response.setBackgroundCheckStatus(user.getBackgroundCheckStatus());
        response.setBackgroundCheckDate(user.getBackgroundCheckDate());
        response.setRequiresBackgroundCheckRenewal(user.requiresBackgroundCheckRenewal());
        response.setSecurityTrainingCompleted(user.getSecurityTrainingCompleted());
        response.setSecurityTrainingDate(user.getSecurityTrainingDate());
        
        // Performance Management
        response.setLastPerformanceReview(user.getLastPerformanceReview());
        response.setNextPerformanceReview(user.getNextPerformanceReview());
        
        // Status Change Audit
        response.setStatusChangeReason(user.getStatusChangeReason());
        response.setStatusChangedBy(user.getStatusChangedBy());
        response.setStatusChangeDate(user.getStatusChangeDate());
        
        // Set status changed by name if available
        if (user.getStatusChangedBy() != null) {
            userRepository.findById(user.getStatusChangedBy())
                    .ifPresent(changer -> response.setStatusChangedByName(changer.getFullName()));
        }
        
        // System Access
        response.setLastLogin(user.getLastLogin());
        response.setFailedLoginAttempts(user.getFailedLoginAttempts());
        response.setAccountLockedUntil(user.getAccountLockedUntil());
        response.setHasSystemAccess(user.hasSystemAccess());
        response.setPayrollEligible(user.isPayrollEligible());
        
        // Compliance
        response.setDataPrivacyConsent(user.getDataPrivacyConsent());
        response.setTermsAcceptedDate(user.getTermsAcceptedDate());
        response.setConfidentialityAgreementSigned(user.getConfidentialityAgreementSigned());
        
        // Legacy fields for backward compatibility
        response.setIsActive(user.getIsActive());
        response.setRoleNames(roleNames);
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        return response;
    }
    
    // Government-Grade Employee Status Management Methods
    
    /**
     * Changes employee status with audit trail
     */
    public EmployeeResponse changeEmployeeStatus(Long employeeId, EmployeeStatus newStatus, 
                                               String reason, Long changedBy) {
        User user = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        // Validate status transition
        if (user.getEmployeeStatus() == newStatus) {
            throw new BadRequestException("Employee is already in " + newStatus.getDisplayName() + " status");
        }
        
        // Update status with audit trail
        user.setEmployeeStatus(newStatus);
        user.setStatusChangeReason(reason);
        user.setStatusChangedBy(changedBy);
        user.setStatusChangeDate(LocalDateTime.now());
        
        // Update legacy isActive field for backward compatibility
        user.setIsActive(newStatus == EmployeeStatus.ACTIVE);
        
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    
    /**
     * Gets employees by status
     */
    public List<EmployeeResponse> getEmployeesByStatus(EmployeeStatus status) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> {
                    // Handle legacy users without employeeStatus
                    if (user.getEmployeeStatus() == null) {
                        // For legacy users, derive status from isActive flag
                        if (status == EmployeeStatus.ACTIVE) {
                            return Boolean.TRUE.equals(user.getIsActive());
                        } else if (status == EmployeeStatus.INACTIVE) {
                            return Boolean.FALSE.equals(user.getIsActive());
                        }
                        return false;
                    }
                    // For users with employeeStatus, match directly
                    return user.getEmployeeStatus() == status;
                })
                .collect(Collectors.toList());
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    /**
     * Gets employees requiring background check renewal
     */
    public List<EmployeeResponse> getEmployeesRequiringBackgroundCheckRenewal() {
        List<User> users = userRepository.findAll().stream()
                .filter(User::requiresBackgroundCheckRenewal)
                .collect(Collectors.toList());
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    /**
     * Gets employees with expiring contracts
     */
    public List<EmployeeResponse> getEmployeesWithExpiringContracts() {
        List<User> users = userRepository.findAll().stream()
                .filter(User::isContractExpiringSoon)
                .collect(Collectors.toList());
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    /**
     * Gets employees in probation
     */
    public List<EmployeeResponse> getEmployeesInProbation() {
        List<User> users = userRepository.findAll().stream()
                .filter(User::isInProbation)
                .collect(Collectors.toList());
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    /**
     * Updates employee security level
     */
    public EmployeeResponse updateSecurityLevel(Long employeeId, ClearanceLevel newLevel, 
                                                String backgroundCheckStatus, LocalDate checkDate) {
        User user = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        user.setClearanceLevel(newLevel);
        user.setBackgroundCheckStatus(backgroundCheckStatus);
        user.setBackgroundCheckDate(checkDate);
        
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    
    /**
     * Gets government-grade statistics
     */
    public GovernmentGradeStats getGovernmentGradeStats() {
        List<User> allUsers = userRepository.findAll();
        
        GovernmentGradeStats stats = new GovernmentGradeStats();
        
        // Status breakdown
        for (EmployeeStatus status : EmployeeStatus.values()) {
            long count = allUsers.stream()
                    .filter(user -> user.getEmployeeStatus() == status)
                    .count();
            stats.addStatusCount(status, count);
        }
        
        // Employment type breakdown
        for (EmploymentType type : EmploymentType.values()) {
            long count = allUsers.stream()
                    .filter(user -> user.getEmploymentType() == type)
                    .count();
            stats.addEmploymentTypeCount(type, count);
        }
        
        // Clearance level breakdown
        for (ClearanceLevel level : ClearanceLevel.values()) {
            long count = allUsers.stream()
                    .filter(user -> user.getClearanceLevel() == level)
                    .count();
            stats.addClearanceLevelCount(level, count);
        }
        
        // Compliance metrics
        stats.setEmployeesRequiringBackgroundCheckRenewal(
                allUsers.stream().filter(User::requiresBackgroundCheckRenewal).count());
        stats.setEmployeesWithExpiringContracts(
                allUsers.stream().filter(User::isContractExpiringSoon).count());
        stats.setEmployeesInProbation(
                allUsers.stream().filter(User::isInProbation).count());
        
        return stats;
    }
    
    // Helper class for government-grade statistics
    public static class GovernmentGradeStats {
        private Map<EmployeeStatus, Long> statusCounts = new HashMap<>();
        private Map<EmploymentType, Long> employmentTypeCounts = new HashMap<>();
        private Map<ClearanceLevel, Long> clearanceLevelCounts = new HashMap<>();
        private long employeesRequiringBackgroundCheckRenewal;
        private long employeesWithExpiringContracts;
        private long employeesInProbation;
        
        // Getters and setters
        public Map<EmployeeStatus, Long> getStatusCounts() { return statusCounts; }
        public void addStatusCount(EmployeeStatus status, long count) { statusCounts.put(status, count); }
        
        public Map<EmploymentType, Long> getEmploymentTypeCounts() { return employmentTypeCounts; }
        public void addEmploymentTypeCount(EmploymentType type, long count) { employmentTypeCounts.put(type, count); }
        
        public Map<ClearanceLevel, Long> getClearanceLevelCounts() { return clearanceLevelCounts; }
        public void addClearanceLevelCount(ClearanceLevel level, long count) { clearanceLevelCounts.put(level, count); }
        
        public long getEmployeesRequiringBackgroundCheckRenewal() { return employeesRequiringBackgroundCheckRenewal; }
        public void setEmployeesRequiringBackgroundCheckRenewal(long count) { this.employeesRequiringBackgroundCheckRenewal = count; }
        
        public long getEmployeesWithExpiringContracts() { return employeesWithExpiringContracts; }
        public void setEmployeesWithExpiringContracts(long count) { this.employeesWithExpiringContracts = count; }
        
        public long getEmployeesInProbation() { return employeesInProbation; }
        public void setEmployeesInProbation(long count) { this.employeesInProbation = count; }
    }
}