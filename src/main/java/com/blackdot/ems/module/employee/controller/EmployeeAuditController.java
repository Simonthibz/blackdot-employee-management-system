package com.blackdot.ems.module.employee.controller;

import com.blackdot.ems.shared.entity.*;
import com.blackdot.ems.module.employee.service.EmployeeAuditService;
import com.blackdot.ems.shared.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Professional Audit Trail Controller
 * Comprehensive audit management and reporting endpoints
 */
@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmployeeAuditController {
    
    @Autowired
    private EmployeeAuditService auditService;
    
    // Audit Trail Queries
    
    /**
     * Get audit trail for specific employee
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR') or (#employeeId == authentication.principal.id)")
    public ResponseEntity<List<EmployeeAuditTrail>> getEmployeeAuditTrail(@PathVariable Long employeeId) {
        List<EmployeeAuditTrail> auditTrail = auditService.getEmployeeAuditTrail(employeeId);
        return ResponseEntity.ok(auditTrail);
    }
    
    /**
     * Get audit trail for specific employee with pagination
     */
    @GetMapping("/employee/{employeeId}/paginated")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR') or (#employeeId == authentication.principal.id)")
    public ResponseEntity<Page<EmployeeAuditTrail>> getEmployeeAuditTrailPaginated(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EmployeeAuditTrail> auditTrail = auditService.getEmployeeAuditTrail(employeeId, page, size);
        return ResponseEntity.ok(auditTrail);
    }
    
    /**
     * Get all audit entries with pagination
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Page<EmployeeAuditTrail>> getAllAuditEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EmployeeAuditTrail> auditEntries = auditService.getAllAuditEntries(page, size);
        return ResponseEntity.ok(auditEntries);
    }
    
    /**
     * Search audit entries
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeAuditTrail>> searchAuditEntries(@RequestParam String searchTerm) {
        List<EmployeeAuditTrail> results = auditService.searchAuditEntries(searchTerm);
        return ResponseEntity.ok(results);
    }
    
    /**
     * Get filtered audit entries
     */
    @GetMapping("/filtered")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Page<EmployeeAuditTrail>> getFilteredAuditEntries(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long performedById,
            @RequestParam(required = false) AuditCategory category,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) AuditSeverity severity,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) ApprovalStatus approvalStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<EmployeeAuditTrail> results = auditService.getFilteredAuditEntries(
                employeeId, performedById, category, action, severity, riskLevel,
                approvalStatus, startDate, endDate, page, size);
        return ResponseEntity.ok(results);
    }
    
    // Specialized Audit Queries
    
    /**
     * Get recent audit activity
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeAuditTrail>> getRecentActivity(
            @RequestParam(defaultValue = "7") int days) {
        List<EmployeeAuditTrail> recentActivity = auditService.getRecentActivity(days);
        return ResponseEntity.ok(recentActivity);
    }
    
    /**
     * Get high-risk events
     */
    @GetMapping("/high-risk")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeAuditTrail>> getHighRiskEvents() {
        List<EmployeeAuditTrail> highRiskEvents = auditService.getHighRiskEvents();
        return ResponseEntity.ok(highRiskEvents);
    }
    
    /**
     * Get security incidents
     */
    @GetMapping("/security-incidents")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeAuditTrail>> getSecurityIncidents() {
        List<EmployeeAuditTrail> securityIncidents = auditService.getSecurityIncidents();
        return ResponseEntity.ok(securityIncidents);
    }
    
    /**
     * Get events requiring approval
     */
    @GetMapping("/pending-approvals")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeAuditTrail>> getPendingApprovals() {
        List<EmployeeAuditTrail> pendingApprovals = auditService.getPendingApprovals();
        return ResponseEntity.ok(pendingApprovals);
    }
    
    /**
     * Get GDPR-relevant events
     */
    @GetMapping("/gdpr-events")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeAuditTrail>> getGdprRelevantEvents() {
        List<EmployeeAuditTrail> gdprEvents = auditService.getGdprRelevantEvents();
        return ResponseEntity.ok(gdprEvents);
    }
    
    /**
     * Get sensitive data events
     */
    @GetMapping("/sensitive-data")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeAuditTrail>> getSensitiveDataEvents() {
        List<EmployeeAuditTrail> sensitiveEvents = auditService.getSensitiveDataEvents();
        return ResponseEntity.ok(sensitiveEvents);
    }
    
    /**
     * Get compliance events
     */
    @GetMapping("/compliance")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeAuditTrail>> getComplianceEvents() {
        List<EmployeeAuditTrail> complianceEvents = auditService.getComplianceEvents();
        return ResponseEntity.ok(complianceEvents);
    }
    
    // Approval Management
    
    /**
     * Approve audit event
     */
    @PostMapping("/{auditId}/approve")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeAuditTrail> approveEvent(
            @PathVariable Long auditId,
            @RequestBody Map<String, Object> request) {
        try {
            Long approverId = Long.valueOf(request.get("approverId").toString());
            String notes = request.getOrDefault("notes", "").toString();
            
            EmployeeAuditTrail approvedEvent = auditService.approveEvent(auditId, approverId, notes);
            return ResponseEntity.ok(approvedEvent);
        } catch (Exception e) {
            throw new BadRequestException("Failed to approve event: " + e.getMessage());
        }
    }
    
    /**
     * Reject audit event
     */
    @PostMapping("/{auditId}/reject")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeAuditTrail> rejectEvent(
            @PathVariable Long auditId,
            @RequestBody Map<String, Object> request) {
        try {
            Long approverId = Long.valueOf(request.get("approverId").toString());
            String notes = request.getOrDefault("notes", "").toString();
            
            EmployeeAuditTrail rejectedEvent = auditService.rejectEvent(auditId, approverId, notes);
            return ResponseEntity.ok(rejectedEvent);
        } catch (Exception e) {
            throw new BadRequestException("Failed to reject event: " + e.getMessage());
        }
    }
    
    // Manual Audit Entry Creation
    
    /**
     * Create manual audit entry
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeAuditTrail> createAuditEntry(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            AuditAction action = AuditAction.valueOf(request.get("action").toString());
            String description = request.get("description").toString();
            Long performedBy = Long.valueOf(request.get("performedBy").toString());
            String ipAddress = request.getOrDefault("ipAddress", "").toString();
            String userAgent = request.getOrDefault("userAgent", "").toString();
            String sessionId = request.getOrDefault("sessionId", "").toString();
            
            EmployeeAuditTrail audit = auditService.logEmployeeAction(
                    employeeId, action, description, performedBy, ipAddress, userAgent, sessionId);
            
            return ResponseEntity.ok(audit);
        } catch (Exception e) {
            throw new BadRequestException("Failed to create audit entry: " + e.getMessage());
        }
    }
    
    // Statistics and Reporting
    
    /**
     * Get comprehensive audit statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAuditStatistics() {
        Map<String, Object> statistics = auditService.getAuditStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Get employee-specific audit statistics
     */
    @GetMapping("/employee/{employeeId}/statistics")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR') or (#employeeId == authentication.principal.id)")
    public ResponseEntity<Map<String, Object>> getEmployeeStatistics(@PathVariable Long employeeId) {
        Map<String, Object> statistics = auditService.getEmployeeStatistics(employeeId);
        return ResponseEntity.ok(statistics);
    }
    
    // Data Management
    
    /**
     * Cleanup expired audit events
     */
    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupExpiredEvents() {
        int deletedCount = auditService.cleanupExpiredEvents();
        Map<String, Object> result = Map.of(
                "deletedCount", deletedCount,
                "message", "Successfully cleaned up " + deletedCount + " expired audit events"
        );
        return ResponseEntity.ok(result);
    }
    
    // Enum Information Endpoints
    
    /**
     * Get all audit actions
     */
    @GetMapping("/enums/actions")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getAuditActions() {
        Map<String, Object> actions = new java.util.HashMap<>();
        for (AuditAction action : AuditAction.values()) {
            Map<String, Object> actionInfo = new java.util.HashMap<>();
            actionInfo.put("name", action.name());
            actionInfo.put("displayName", action.getDisplayName());
            actionInfo.put("description", action.getDescription());
            actionInfo.put("isSecurityRelated", action.isSecurityRelated());
            actionInfo.put("isPersonalDataRelated", action.isPersonalDataRelated());
            actionInfo.put("isEmploymentRelated", action.isEmploymentRelated());
            actionInfo.put("isComplianceRelated", action.isComplianceRelated());
            actionInfo.put("requiresApproval", action.requiresApproval());
            actionInfo.put("defaultSeverity", action.getDefaultSeverity().name());
            actionInfo.put("defaultRiskLevel", action.getDefaultRiskLevel().name());
            actions.put(action.name(), actionInfo);
        }
        return ResponseEntity.ok(actions);
    }
    
    /**
     * Get all audit categories
     */
    @GetMapping("/enums/categories")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getAuditCategories() {
        Map<String, Object> categories = new java.util.HashMap<>();
        for (AuditCategory category : AuditCategory.values()) {
            Map<String, Object> categoryInfo = new java.util.HashMap<>();
            categoryInfo.put("name", category.name());
            categoryInfo.put("displayName", category.getDisplayName());
            categoryInfo.put("description", category.getDescription());
            categoryInfo.put("icon", category.getIcon());
            categoryInfo.put("badgeClass", category.getBadgeClass());
            categoryInfo.put("isHighRiskCategory", category.isHighRiskCategory());
            categoryInfo.put("requiresRetention", category.requiresRetention());
            categoryInfo.put("defaultRetentionDays", category.getDefaultRetentionDays());
            categories.put(category.name(), categoryInfo);
        }
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get all audit severities
     */
    @GetMapping("/enums/severities")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getAuditSeverities() {
        Map<String, Object> severities = new java.util.HashMap<>();
        for (AuditSeverity severity : AuditSeverity.values()) {
            Map<String, Object> severityInfo = new java.util.HashMap<>();
            severityInfo.put("name", severity.name());
            severityInfo.put("displayName", severity.getDisplayName());
            severityInfo.put("description", severity.getDescription());
            severityInfo.put("textClass", severity.getTextClass());
            severityInfo.put("badgeClass", severity.getBadgeClass());
            severityInfo.put("icon", severity.getIcon());
            severities.put(severity.name(), severityInfo);
        }
        return ResponseEntity.ok(severities);
    }
    
    /**
     * Get all risk levels
     */
    @GetMapping("/enums/risk-levels")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getRiskLevels() {
        Map<String, Object> riskLevels = new java.util.HashMap<>();
        for (RiskLevel riskLevel : RiskLevel.values()) {
            Map<String, Object> riskLevelInfo = new java.util.HashMap<>();
            riskLevelInfo.put("name", riskLevel.name());
            riskLevelInfo.put("displayName", riskLevel.getDisplayName());
            riskLevelInfo.put("description", riskLevel.getDescription());
            riskLevelInfo.put("badgeClass", riskLevel.getBadgeClass());
            riskLevelInfo.put("icon", riskLevel.getIcon());
            riskLevelInfo.put("requiresImmediateAttention", riskLevel.requiresImmediateAttention());
            riskLevelInfo.put("requiresApproval", riskLevel.requiresApproval());
            riskLevels.put(riskLevel.name(), riskLevelInfo);
        }
        return ResponseEntity.ok(riskLevels);
    }
    
    /**
     * Get all approval statuses
     */
    @GetMapping("/enums/approval-statuses")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getApprovalStatuses() {
        Map<String, Object> approvalStatuses = new java.util.HashMap<>();
        for (ApprovalStatus status : ApprovalStatus.values()) {
            Map<String, Object> statusInfo = new java.util.HashMap<>();
            statusInfo.put("name", status.name());
            statusInfo.put("displayName", status.getDisplayName());
            statusInfo.put("description", status.getDescription());
            statusInfo.put("badgeClass", status.getBadgeClass());
            statusInfo.put("icon", status.getIcon());
            statusInfo.put("isActive", status.isActive());
            statusInfo.put("isCompleted", status.isCompleted());
            statusInfo.put("requiresAction", status.requiresAction());
            approvalStatuses.put(status.name(), statusInfo);
        }
        return ResponseEntity.ok(approvalStatuses);
    }
}