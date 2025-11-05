package com.blackdot.ems.module.employee.repository;

import com.blackdot.ems.shared.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Professional Audit Trail Repository
 * Provides comprehensive data access methods for audit trail management
 */
@Repository
public interface EmployeeAuditTrailRepository extends JpaRepository<EmployeeAuditTrail, Long> {
    
    // Basic queries by employee
    List<EmployeeAuditTrail> findByEmployeeIdOrderByPerformedAtDesc(Long employeeId);
    
    Page<EmployeeAuditTrail> findByEmployeeIdOrderByPerformedAtDesc(Long employeeId, Pageable pageable);
    
    // Queries by performer
    List<EmployeeAuditTrail> findByPerformedByIdOrderByPerformedAtDesc(Long performedById);
    
    Page<EmployeeAuditTrail> findByPerformedByIdOrderByPerformedAtDesc(Long performedById, Pageable pageable);
    
    // Queries by action and category
    List<EmployeeAuditTrail> findByActionOrderByPerformedAtDesc(AuditAction action);
    
    List<EmployeeAuditTrail> findByCategoryOrderByPerformedAtDesc(AuditCategory category);
    
    Page<EmployeeAuditTrail> findByActionOrderByPerformedAtDesc(AuditAction action, Pageable pageable);
    
    Page<EmployeeAuditTrail> findByCategoryOrderByPerformedAtDesc(AuditCategory category, Pageable pageable);
    
    // Queries by severity and risk level
    List<EmployeeAuditTrail> findBySeverityOrderByPerformedAtDesc(AuditSeverity severity);
    
    List<EmployeeAuditTrail> findByRiskLevelOrderByPerformedAtDesc(RiskLevel riskLevel);
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.severity IN ('HIGH', 'CRITICAL') ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findHighSeverityEvents();
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.riskLevel IN ('HIGH', 'CRITICAL') ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findHighRiskEvents();
    
    // Time-based queries
    List<EmployeeAuditTrail> findByPerformedAtBetweenOrderByPerformedAtDesc(
            LocalDateTime startDate, LocalDateTime endDate);
    
    Page<EmployeeAuditTrail> findByPerformedAtBetweenOrderByPerformedAtDesc(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findRecentEvents(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate ORDER BY a.performedAt DESC")
    Page<EmployeeAuditTrail> findRecentEvents(@Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    // Approval-related queries
    List<EmployeeAuditTrail> findByApprovalStatusOrderByPerformedAtDesc(ApprovalStatus approvalStatus);
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.approvalRequired = true AND a.approvalStatus = 'PENDING' ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findPendingApprovals();
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.approvalRequired = true AND a.approvalStatus = 'PENDING' ORDER BY a.performedAt DESC")
    Page<EmployeeAuditTrail> findPendingApprovals(Pageable pageable);
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.approvedBy.id = :approverId ORDER BY a.approvedAt DESC")
    List<EmployeeAuditTrail> findByApproverId(@Param("approverId") Long approverId);
    
    // Security and compliance queries
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.action IN ('UNAUTHORIZED_ACCESS', 'DATA_BREACH_DETECTED', 'SUSPICIOUS_ACTIVITY') ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findSecurityIncidents();
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.gdprRelevant = true ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findGdprRelevantEvents();
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.isSensitiveData = true ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findSensitiveDataEvents();
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.category IN ('COMPLIANCE', 'DATA_PRIVACY', 'SECURITY') ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findComplianceEvents();
    
    // Data retention queries
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.createdAt < :cutoffDate")
    List<EmployeeAuditTrail> findEventsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query(value = "SELECT * FROM employee_audit_trail WHERE created_at + INTERVAL retention_period_days DAY < NOW()", nativeQuery = true)
    List<EmployeeAuditTrail> findExpiredRetentionEvents();
    
    // Search queries
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.employee.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.employee.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.performedBy.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.performedBy.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.fieldName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> searchAuditEvents(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.employee.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.employee.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.performedBy.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.performedBy.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.fieldName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY a.performedAt DESC")
    Page<EmployeeAuditTrail> searchAuditEvents(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Field-specific change tracking
    List<EmployeeAuditTrail> findByEmployeeIdAndFieldNameOrderByPerformedAtDesc(Long employeeId, String fieldName);
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.employee.id = :employeeId AND a.fieldName = :fieldName ORDER BY a.performedAt DESC LIMIT 1")
    EmployeeAuditTrail findLastChangeForField(@Param("employeeId") Long employeeId, @Param("fieldName") String fieldName);
    
    // IP Address and session tracking
    List<EmployeeAuditTrail> findByIpAddressOrderByPerformedAtDesc(String ipAddress);
    
    List<EmployeeAuditTrail> findBySessionIdOrderByPerformedAtDesc(String sessionId);
    
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE a.ipAddress = :ipAddress AND a.performedAt >= :startDate ORDER BY a.performedAt DESC")
    List<EmployeeAuditTrail> findByIpAddressAndTimeRange(@Param("ipAddress") String ipAddress, @Param("startDate") LocalDateTime startDate);
    
    // Statistical queries
    @Query("SELECT COUNT(a) FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate")
    Long countEventsSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT a.action, COUNT(a) FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate GROUP BY a.action ORDER BY COUNT(a) DESC")
    List<Object[]> getActionStatistics(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT a.category, COUNT(a) FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate GROUP BY a.category ORDER BY COUNT(a) DESC")
    List<Object[]> getCategoryStatistics(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT a.severity, COUNT(a) FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate GROUP BY a.severity ORDER BY COUNT(a) DESC")
    List<Object[]> getSeverityStatistics(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT a.riskLevel, COUNT(a) FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate GROUP BY a.riskLevel ORDER BY COUNT(a) DESC")
    List<Object[]> getRiskLevelStatistics(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT DATE(a.performedAt), COUNT(a) FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate GROUP BY DATE(a.performedAt) ORDER BY DATE(a.performedAt)")
    List<Object[]> getDailyActivityStatistics(@Param("startDate") LocalDateTime startDate);
    
    // Employee-specific statistics
    @Query("SELECT COUNT(a) FROM EmployeeAuditTrail a WHERE a.employee.id = :employeeId")
    Long countEventsByEmployee(@Param("employeeId") Long employeeId);
    
    @Query("SELECT a.action, COUNT(a) FROM EmployeeAuditTrail a WHERE a.employee.id = :employeeId GROUP BY a.action ORDER BY COUNT(a) DESC")
    List<Object[]> getEmployeeActionStatistics(@Param("employeeId") Long employeeId);
    
    // User activity tracking
    @Query("SELECT COUNT(a) FROM EmployeeAuditTrail a WHERE a.performedBy.id = :userId AND a.performedAt >= :startDate")
    Long countUserActivitySince(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT a.performedBy, COUNT(a) FROM EmployeeAuditTrail a WHERE a.performedAt >= :startDate GROUP BY a.performedBy ORDER BY COUNT(a) DESC")
    List<Object[]> getMostActiveUsers(@Param("startDate") LocalDateTime startDate);
    
    // Complex filtering query
    @Query("SELECT a FROM EmployeeAuditTrail a WHERE " +
           "(:employeeId IS NULL OR a.employee.id = :employeeId) AND " +
           "(:performedById IS NULL OR a.performedBy.id = :performedById) AND " +
           "(:category IS NULL OR a.category = :category) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:severity IS NULL OR a.severity = :severity) AND " +
           "(:riskLevel IS NULL OR a.riskLevel = :riskLevel) AND " +
           "(:approvalStatus IS NULL OR a.approvalStatus = :approvalStatus) AND " +
           "(:startDate IS NULL OR a.performedAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.performedAt <= :endDate) " +
           "ORDER BY a.performedAt DESC")
    Page<EmployeeAuditTrail> findWithFilters(
            @Param("employeeId") Long employeeId,
            @Param("performedById") Long performedById,
            @Param("category") AuditCategory category,
            @Param("action") AuditAction action,
            @Param("severity") AuditSeverity severity,
            @Param("riskLevel") RiskLevel riskLevel,
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}