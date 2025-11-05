package com.blackdot.ems.module.employee.service;

import com.blackdot.ems.shared.entity.*;
import com.blackdot.ems.module.employee.repository.EmployeeAuditTrailRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Professional Audit Service
 * Comprehensive audit trail management for employee-related actions
 */
@Service
@Transactional
public class EmployeeAuditService {
    
    @Autowired
    private EmployeeAuditTrailRepository auditRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create audit trail entry
     */
    public EmployeeAuditTrail createAuditEntry(EmployeeAuditTrail.Builder auditBuilder) {
        EmployeeAuditTrail audit = auditBuilder.build();
        return auditRepository.save(audit);
    }
    
    /**
     * Log employee action with full context
     */
    public EmployeeAuditTrail logEmployeeAction(
            Long employeeId, 
            AuditAction action, 
            String description, 
            Long performedBy,
            String ipAddress,
            String userAgent,
            String sessionId) {
        
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        User performer = userRepository.findById(performedBy)
                .orElseThrow(() -> new RuntimeException("Performer not found"));
        
        EmployeeAuditTrail audit = EmployeeAuditTrail.builder()
                .employee(employee)
                .action(action)
                .category(getCategoryForAction(action))
                .description(description)
                .performedBy(performer)
                .sessionInfo(ipAddress, userAgent, sessionId)
                .severity(action.getDefaultSeverity())
                .riskLevel(action.getDefaultRiskLevel())
                .approvalRequired(action.requiresApproval())
                .sensitiveData(action.isPersonalDataRelated())
                .gdprRelevant(action.isPersonalDataRelated())
                .build();
        
        return auditRepository.save(audit);
    }
    
    /**
     * Log field change with old and new values
     */
    public EmployeeAuditTrail logFieldChange(
            Long employeeId,
            String fieldName,
            String oldValue,
            String newValue,
            Long performedBy,
            String sessionInfo) {
        
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        User performer = userRepository.findById(performedBy)
                .orElseThrow(() -> new RuntimeException("Performer not found"));
        
        AuditAction action = getActionForFieldChange(fieldName);
        
        EmployeeAuditTrail audit = EmployeeAuditTrail.builder()
                .employee(employee)
                .action(action)
                .category(getCategoryForAction(action))
                .description(String.format("Field '%s' changed from '%s' to '%s'", fieldName, oldValue, newValue))
                .fieldChange(fieldName, oldValue, newValue)
                .performedBy(performer)
                .severity(action.getDefaultSeverity())
                .riskLevel(action.getDefaultRiskLevel())
                .sensitiveData(isFieldSensitive(fieldName))
                .gdprRelevant(isFieldGdprRelevant(fieldName))
                .build();
        
        return auditRepository.save(audit);
    }
    
    /**
     * Get audit trail for specific employee
     */
    public List<EmployeeAuditTrail> getEmployeeAuditTrail(Long employeeId) {
        return auditRepository.findByEmployeeIdOrderByPerformedAtDesc(employeeId);
    }
    
    /**
     * Get audit trail for specific employee with pagination
     */
    public Page<EmployeeAuditTrail> getEmployeeAuditTrail(Long employeeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditRepository.findByEmployeeIdOrderByPerformedAtDesc(employeeId, pageable);
    }
    
    /**
     * Get all audit entries with pagination
     */
    public Page<EmployeeAuditTrail> getAllAuditEntries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditRepository.findAll(pageable);
    }
    
    /**
     * Search audit entries
     */
    public List<EmployeeAuditTrail> searchAuditEntries(String searchTerm) {
        return auditRepository.searchAuditEvents(searchTerm);
    }
    
    /**
     * Get filtered audit entries
     */
    public Page<EmployeeAuditTrail> getFilteredAuditEntries(
            Long employeeId,
            Long performedById,
            AuditCategory category,
            AuditAction action,
            AuditSeverity severity,
            RiskLevel riskLevel,
            ApprovalStatus approvalStatus,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return auditRepository.findWithFilters(
                employeeId, performedById, category, action, severity, riskLevel,
                approvalStatus, startDate, endDate, pageable);
    }
    
    /**
     * Get recent audit activity
     */
    public List<EmployeeAuditTrail> getRecentActivity(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return auditRepository.findRecentEvents(cutoffDate);
    }
    
    /**
     * Get high-risk events
     */
    public List<EmployeeAuditTrail> getHighRiskEvents() {
        return auditRepository.findHighRiskEvents();
    }
    
    /**
     * Get security incidents
     */
    public List<EmployeeAuditTrail> getSecurityIncidents() {
        return auditRepository.findSecurityIncidents();
    }
    
    /**
     * Get events requiring approval
     */
    public List<EmployeeAuditTrail> getPendingApprovals() {
        return auditRepository.findPendingApprovals();
    }
    
    /**
     * Approve audit event
     */
    public EmployeeAuditTrail approveEvent(Long auditId, Long approverId, String notes) {
        EmployeeAuditTrail audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit entry not found"));
        
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        audit.setApprovalStatus(ApprovalStatus.APPROVED);
        audit.setApprovedBy(approver);
        audit.setApprovedAt(LocalDateTime.now());
        audit.setApprovalNotes(notes);
        
        return auditRepository.save(audit);
    }
    
    /**
     * Reject audit event
     */
    public EmployeeAuditTrail rejectEvent(Long auditId, Long approverId, String notes) {
        EmployeeAuditTrail audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit entry not found"));
        
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        audit.setApprovalStatus(ApprovalStatus.REJECTED);
        audit.setApprovedBy(approver);
        audit.setApprovedAt(LocalDateTime.now());
        audit.setApprovalNotes(notes);
        
        return auditRepository.save(audit);
    }
    
    /**
     * Get comprehensive audit statistics
     */
    public Map<String, Object> getAuditStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // Basic counts
        stats.put("totalEvents", auditRepository.count());
        stats.put("recentEvents", auditRepository.countEventsSince(thirtyDaysAgo));
        stats.put("pendingApprovals", auditRepository.findPendingApprovals().size());
        stats.put("highRiskEvents", auditRepository.findHighRiskEvents().size());
        stats.put("securityIncidents", auditRepository.findSecurityIncidents().size());
        
        // Action statistics
        List<Object[]> actionStats = auditRepository.getActionStatistics(thirtyDaysAgo);
        Map<String, Long> actionCounts = actionStats.stream()
                .collect(Collectors.toMap(
                        arr -> ((AuditAction) arr[0]).getDisplayName(),
                        arr -> (Long) arr[1]
                ));
        stats.put("actionStatistics", actionCounts);
        
        // Category statistics
        List<Object[]> categoryStats = auditRepository.getCategoryStatistics(thirtyDaysAgo);
        Map<String, Long> categoryCounts = categoryStats.stream()
                .collect(Collectors.toMap(
                        arr -> ((AuditCategory) arr[0]).getDisplayName(),
                        arr -> (Long) arr[1]
                ));
        stats.put("categoryStatistics", categoryCounts);
        
        // Severity statistics
        List<Object[]> severityStats = auditRepository.getSeverityStatistics(thirtyDaysAgo);
        Map<String, Long> severityCounts = severityStats.stream()
                .collect(Collectors.toMap(
                        arr -> ((AuditSeverity) arr[0]).getDisplayName(),
                        arr -> (Long) arr[1]
                ));
        stats.put("severityStatistics", severityCounts);
        
        // Daily activity
        List<Object[]> dailyStats = auditRepository.getDailyActivityStatistics(thirtyDaysAgo);
        Map<String, Long> dailyActivity = dailyStats.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));
        stats.put("dailyActivity", dailyActivity);
        
        // Most active users
        List<Object[]> userStats = auditRepository.getMostActiveUsers(thirtyDaysAgo);
        Map<String, Long> userActivity = userStats.stream()
                .limit(10) // Top 10 users
                .collect(Collectors.toMap(
                        arr -> {
                            User user = (User) arr[0];
                            return user.getFirstName() + " " + user.getLastName();
                        },
                        arr -> (Long) arr[1]
                ));
        stats.put("mostActiveUsers", userActivity);
        
        return stats;
    }
    
    /**
     * Get employee-specific statistics
     */
    public Map<String, Object> getEmployeeStatistics(Long employeeId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalEvents", auditRepository.countEventsByEmployee(employeeId));
        
        List<Object[]> actionStats = auditRepository.getEmployeeActionStatistics(employeeId);
        Map<String, Long> actionCounts = actionStats.stream()
                .collect(Collectors.toMap(
                        arr -> ((AuditAction) arr[0]).getDisplayName(),
                        arr -> (Long) arr[1]
                ));
        stats.put("actionStatistics", actionCounts);
        
        return stats;
    }
    
    /**
     * Get GDPR-relevant events for data subject requests
     */
    public List<EmployeeAuditTrail> getGdprRelevantEvents() {
        return auditRepository.findGdprRelevantEvents();
    }
    
    /**
     * Get sensitive data events
     */
    public List<EmployeeAuditTrail> getSensitiveDataEvents() {
        return auditRepository.findSensitiveDataEvents();
    }
    
    /**
     * Get compliance events
     */
    public List<EmployeeAuditTrail> getComplianceEvents() {
        return auditRepository.findComplianceEvents();
    }
    
    /**
     * Data retention cleanup
     */
    public int cleanupExpiredEvents() {
        List<EmployeeAuditTrail> expiredEvents = auditRepository.findExpiredRetentionEvents();
        auditRepository.deleteAll(expiredEvents);
        return expiredEvents.size();
    }
    
    // Helper methods
    
    private AuditCategory getCategoryForAction(AuditAction action) {
        if (action.isSecurityRelated()) return AuditCategory.SECURITY;
        if (action.isPersonalDataRelated()) return AuditCategory.PERSONAL_DATA;
        if (action.isEmploymentRelated()) return AuditCategory.EMPLOYMENT_STATUS;
        if (action.isComplianceRelated()) return AuditCategory.COMPLIANCE;
        return AuditCategory.EMPLOYEE_MANAGEMENT;
    }
    
    private AuditAction getActionForFieldChange(String fieldName) {
        return switch (fieldName.toLowerCase()) {
            case "firstname", "lastname", "dateofbirth", "personalid" -> AuditAction.PERSONAL_INFO_UPDATED;
            case "email", "phonenumber", "emergencycontact" -> AuditAction.CONTACT_INFO_UPDATED;
            case "address", "city", "postalcode" -> AuditAction.ADDRESS_UPDATED;
            case "bankdetails", "accountnumber", "taxnumber" -> AuditAction.BANK_DETAILS_UPDATED;
            case "status", "employmentstatus" -> AuditAction.STATUS_CHANGED;
            case "securitylevel", "clearancelevel" -> AuditAction.SECURITY_LEVEL_CHANGED;
            case "salary", "basicsalary", "allowances" -> AuditAction.SALARY_ADJUSTMENT;
            case "position", "jobTitle", "department" -> AuditAction.POSITION_CHANGE;
            case "reportingmanager", "supervisor" -> AuditAction.MANAGER_CHANGED;
            default -> AuditAction.EMPLOYEE_UPDATED;
        };
    }
    
    private boolean isFieldSensitive(String fieldName) {
        return Set.of("personalid", "dateofbirth", "salary", "basicsalary", 
                     "bankdetails", "accountnumber", "taxnumber", "emergencycontact",
                     "securitylevel", "clearancelevel").contains(fieldName.toLowerCase());
    }
    
    private boolean isFieldGdprRelevant(String fieldName) {
        return Set.of("firstname", "lastname", "email", "phonenumber", "address", 
                     "personalid", "dateofbirth", "emergencycontact").contains(fieldName.toLowerCase());
    }
}