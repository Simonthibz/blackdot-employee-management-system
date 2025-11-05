package com.blackdot.ems.shared.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Professional Audit Trail Entity
 * Tracks all significant changes and actions within the employee management system
 */
@Entity
@Table(name = "employee_audit_trail")
public class EmployeeAuditTrail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditCategory category;
    
    @Column(nullable = false, length = 500)
    private String description;
    
    @Column(name = "field_name", length = 100)
    private String fieldName;
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;
    
    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditSeverity severity;
    
    @Column(name = "risk_level", length = 20)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    
    @Column(name = "compliance_impact", columnDefinition = "TEXT")
    private String complianceImpact;
    
    @Column(name = "approval_required")
    private Boolean approvalRequired = false;
    
    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "approval_notes", columnDefinition = "TEXT")
    private String approvalNotes;
    
    @Column(name = "reference_id", length = 100)
    private String referenceId;
    
    @Column(name = "business_justification", columnDefinition = "TEXT")
    private String businessJustification;
    
    @Column(name = "additional_metadata", columnDefinition = "TEXT")
    private String additionalMetadata;
    
    @Column(name = "retention_period_days")
    private Integer retentionPeriodDays = 2555; // 7 years default
    
    @Column(name = "is_sensitive_data")
    private Boolean isSensitiveData = false;
    
    @Column(name = "gdpr_relevant")
    private Boolean gdprRelevant = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (performedAt == null) {
            performedAt = LocalDateTime.now();
        }
        if (severity == null) {
            severity = AuditSeverity.MEDIUM;
        }
        if (riskLevel == null) {
            riskLevel = RiskLevel.LOW;
        }
        if (approvalStatus == null) {
            approvalStatus = approvalRequired ? ApprovalStatus.PENDING : ApprovalStatus.NOT_REQUIRED;
        }
    }
    
    // Constructors
    public EmployeeAuditTrail() {
    }
    
    public EmployeeAuditTrail(User employee, AuditAction action, AuditCategory category, String description, User performedBy) {
        this.employee = employee;
        this.action = action;
        this.category = category;
        this.description = description;
        this.performedBy = performedBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getEmployee() {
        return employee;
    }
    
    public void setEmployee(User employee) {
        this.employee = employee;
    }
    
    public AuditAction getAction() {
        return action;
    }
    
    public void setAction(AuditAction action) {
        this.action = action;
    }
    
    public AuditCategory getCategory() {
        return category;
    }
    
    public void setCategory(AuditCategory category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
    
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
    
    public User getPerformedBy() {
        return performedBy;
    }
    
    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }
    
    public LocalDateTime getPerformedAt() {
        return performedAt;
    }
    
    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public AuditSeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(AuditSeverity severity) {
        this.severity = severity;
    }
    
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public String getComplianceImpact() {
        return complianceImpact;
    }
    
    public void setComplianceImpact(String complianceImpact) {
        this.complianceImpact = complianceImpact;
    }
    
    public Boolean getApprovalRequired() {
        return approvalRequired;
    }
    
    public void setApprovalRequired(Boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }
    
    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public User getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public String getApprovalNotes() {
        return approvalNotes;
    }
    
    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getBusinessJustification() {
        return businessJustification;
    }
    
    public void setBusinessJustification(String businessJustification) {
        this.businessJustification = businessJustification;
    }
    
    public String getAdditionalMetadata() {
        return additionalMetadata;
    }
    
    public void setAdditionalMetadata(String additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }
    
    public Integer getRetentionPeriodDays() {
        return retentionPeriodDays;
    }
    
    public void setRetentionPeriodDays(Integer retentionPeriodDays) {
        this.retentionPeriodDays = retentionPeriodDays;
    }
    
    public Boolean getIsSensitiveData() {
        return isSensitiveData;
    }
    
    public void setIsSensitiveData(Boolean isSensitiveData) {
        this.isSensitiveData = isSensitiveData;
    }
    
    public Boolean getGdprRelevant() {
        return gdprRelevant;
    }
    
    public void setGdprRelevant(Boolean gdprRelevant) {
        this.gdprRelevant = gdprRelevant;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Utility methods
    public boolean isHighRisk() {
        return riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL;
    }
    
    public boolean requiresApproval() {
        return approvalRequired != null && approvalRequired;
    }
    
    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }
    
    public boolean isPendingApproval() {
        return approvalStatus == ApprovalStatus.PENDING;
    }
    
    public boolean isRejected() {
        return approvalStatus == ApprovalStatus.REJECTED;
    }
    
    public String getActionDisplayName() {
        return action != null ? action.getDisplayName() : "Unknown Action";
    }
    
    public String getCategoryDisplayName() {
        return category != null ? category.getDisplayName() : "Unknown Category";
    }
    
    public String getSeverityBadgeClass() {
        if (severity == null) return "bg-secondary";
        
        return switch (severity) {
            case LOW -> "bg-info";
            case MEDIUM -> "bg-warning";
            case HIGH -> "bg-danger";
            case CRITICAL -> "bg-dark";
        };
    }
    
    public String getRiskLevelBadgeClass() {
        if (riskLevel == null) return "bg-secondary";
        
        return switch (riskLevel) {
            case LOW -> "bg-success";
            case MEDIUM -> "bg-warning";
            case HIGH -> "bg-danger";
            case CRITICAL -> "bg-dark";
        };
    }
    
    public String getApprovalStatusBadgeClass() {
        if (approvalStatus == null) return "bg-secondary";
        
        return switch (approvalStatus) {
            case NOT_REQUIRED -> "bg-light text-dark";
            case PENDING -> "bg-warning";
            case APPROVED -> "bg-success";
            case REJECTED -> "bg-danger";
            case EXPIRED -> "bg-secondary";
        };
    }
    
    public boolean isRetentionExpired() {
        if (retentionPeriodDays == null || retentionPeriodDays <= 0) {
            return false;
        }
        
        LocalDateTime expiryDate = createdAt.plusDays(retentionPeriodDays);
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    public LocalDateTime getRetentionExpiryDate() {
        if (retentionPeriodDays == null || retentionPeriodDays <= 0) {
            return null;
        }
        
        return createdAt.plusDays(retentionPeriodDays);
    }
    
    // Builder pattern for easier creation
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private EmployeeAuditTrail audit = new EmployeeAuditTrail();
        
        public Builder employee(User employee) {
            audit.employee = employee;
            return this;
        }
        
        public Builder action(AuditAction action) {
            audit.action = action;
            return this;
        }
        
        public Builder category(AuditCategory category) {
            audit.category = category;
            return this;
        }
        
        public Builder description(String description) {
            audit.description = description;
            return this;
        }
        
        public Builder fieldChange(String fieldName, String oldValue, String newValue) {
            audit.fieldName = fieldName;
            audit.oldValue = oldValue;
            audit.newValue = newValue;
            return this;
        }
        
        public Builder performedBy(User performedBy) {
            audit.performedBy = performedBy;
            return this;
        }
        
        public Builder sessionInfo(String ipAddress, String userAgent, String sessionId) {
            audit.ipAddress = ipAddress;
            audit.userAgent = userAgent;
            audit.sessionId = sessionId;
            return this;
        }
        
        public Builder severity(AuditSeverity severity) {
            audit.severity = severity;
            return this;
        }
        
        public Builder riskLevel(RiskLevel riskLevel) {
            audit.riskLevel = riskLevel;
            return this;
        }
        
        public Builder approvalRequired(boolean required) {
            audit.approvalRequired = required;
            return this;
        }
        
        public Builder businessJustification(String justification) {
            audit.businessJustification = justification;
            return this;
        }
        
        public Builder referenceId(String referenceId) {
            audit.referenceId = referenceId;
            return this;
        }
        
        public Builder sensitiveData(boolean sensitive) {
            audit.isSensitiveData = sensitive;
            return this;
        }
        
        public Builder gdprRelevant(boolean relevant) {
            audit.gdprRelevant = relevant;
            return this;
        }
        
        public EmployeeAuditTrail build() {
            return audit;
        }
    }
}