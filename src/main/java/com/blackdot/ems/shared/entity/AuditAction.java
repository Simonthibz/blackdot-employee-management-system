package com.blackdot.ems.shared.entity;

/**
 * Audit Action Types
 * Defines all possible actions that can be audited within the system
 */
public enum AuditAction {
    
    // Employee Management Actions
    EMPLOYEE_CREATED("Employee Created", "New employee record created"),
    EMPLOYEE_UPDATED("Employee Updated", "Employee information modified"),
    EMPLOYEE_DELETED("Employee Deleted", "Employee record deleted"),
    EMPLOYEE_ACTIVATED("Employee Activated", "Employee status set to active"),
    EMPLOYEE_DEACTIVATED("Employee Deactivated", "Employee status set to inactive"),
    EMPLOYEE_SUSPENDED("Employee Suspended", "Employee temporarily suspended"),
    EMPLOYEE_TERMINATED("Employee Terminated", "Employee permanently terminated"),
    EMPLOYEE_REINSTATED("Employee Reinstated", "Previously terminated employee reinstated"),
    
    // Authentication & Security Actions
    LOGIN_SUCCESSFUL("Login Successful", "User successfully logged in"),
    LOGIN_FAILED("Login Failed", "User login attempt failed"),
    LOGOUT("Logout", "User logged out"),
    PASSWORD_CHANGED("Password Changed", "User password was changed"),
    PASSWORD_RESET("Password Reset", "User password was reset"),
    ACCOUNT_LOCKED("Account Locked", "User account was locked"),
    ACCOUNT_UNLOCKED("Account Unlocked", "User account was unlocked"),
    PERMISSION_GRANTED("Permission Granted", "User permission granted"),
    PERMISSION_REVOKED("Permission Revoked", "User permission revoked"),
    ROLE_ASSIGNED("Role Assigned", "User role assigned"),
    ROLE_REMOVED("Role Removed", "User role removed"),
    
    // Personal Information Changes
    PERSONAL_INFO_UPDATED("Personal Info Updated", "Personal information changed"),
    CONTACT_INFO_UPDATED("Contact Info Updated", "Contact information changed"),
    ADDRESS_UPDATED("Address Updated", "Address information changed"),
    EMERGENCY_CONTACT_UPDATED("Emergency Contact Updated", "Emergency contact information changed"),
    BANK_DETAILS_UPDATED("Bank Details Updated", "Banking information changed"),
    TAX_INFO_UPDATED("Tax Info Updated", "Tax information changed"),
    
    // Employment Status Changes
    STATUS_CHANGED("Status Changed", "Employment status changed"),
    CLASSIFICATION_CHANGED("Classification Changed", "Employment classification changed"),
    SECURITY_LEVEL_CHANGED("Security Level Changed", "Security clearance level changed"),
    DEPARTMENT_TRANSFER("Department Transfer", "Employee transferred to different department"),
    POSITION_CHANGE("Position Change", "Employee position or title changed"),
    SALARY_ADJUSTMENT("Salary Adjustment", "Employee salary or compensation changed"),
    MANAGER_ASSIGNED("Manager Assigned", "Reporting manager assigned"),
    MANAGER_CHANGED("Manager Changed", "Reporting manager changed"),
    
    // Contract & Documentation
    CONTRACT_CREATED("Contract Created", "Employment contract created"),
    CONTRACT_UPDATED("Contract Updated", "Employment contract modified"),
    CONTRACT_RENEWED("Contract Renewed", "Employment contract renewed"),
    CONTRACT_TERMINATED("Contract Terminated", "Employment contract terminated"),
    DOCUMENT_UPLOADED("Document Uploaded", "Document uploaded to employee file"),
    DOCUMENT_DELETED("Document Deleted", "Document deleted from employee file"),
    DOCUMENT_ACCESSED("Document Accessed", "Employee document accessed"),
    
    // Performance & Reviews
    PERFORMANCE_REVIEW_CREATED("Performance Review Created", "Performance review initiated"),
    PERFORMANCE_REVIEW_COMPLETED("Performance Review Completed", "Performance review completed"),
    TRAINING_ASSIGNED("Training Assigned", "Training program assigned"),
    TRAINING_COMPLETED("Training Completed", "Training program completed"),
    CERTIFICATION_ADDED("Certification Added", "Professional certification added"),
    CERTIFICATION_EXPIRED("Certification Expired", "Professional certification expired"),
    
    // Lifecycle Events
    ONBOARDING_STARTED("Onboarding Started", "Employee onboarding process initiated"),
    ONBOARDING_COMPLETED("Onboarding Completed", "Employee onboarding process completed"),
    PROBATION_STARTED("Probation Started", "Probation period initiated"),
    PROBATION_COMPLETED("Probation Completed", "Probation period successfully completed"),
    PROBATION_EXTENDED("Probation Extended", "Probation period extended"),
    PROBATION_FAILED("Probation Failed", "Probation period failed"),
    
    // Leave & Attendance
    LEAVE_REQUESTED("Leave Requested", "Leave request submitted"),
    LEAVE_APPROVED("Leave Approved", "Leave request approved"),
    LEAVE_REJECTED("Leave Rejected", "Leave request rejected"),
    LEAVE_CANCELLED("Leave Cancelled", "Leave request cancelled"),
    ATTENDANCE_RECORDED("Attendance Recorded", "Attendance manually recorded"),
    ATTENDANCE_CORRECTED("Attendance Corrected", "Attendance record corrected"),
    
    // System & Data Actions
    DATA_EXPORT("Data Export", "Employee data exported"),
    DATA_IMPORT("Data Import", "Employee data imported"),
    BULK_UPDATE("Bulk Update", "Multiple employee records updated"),
    SYSTEM_BACKUP("System Backup", "Employee data backed up"),
    DATA_MIGRATION("Data Migration", "Employee data migrated"),
    GDPR_REQUEST("GDPR Request", "GDPR-related data request processed"),
    DATA_RETENTION_APPLIED("Data Retention Applied", "Data retention policy applied"),
    DATA_ANONYMIZED("Data Anonymized", "Personal data anonymized"),
    
    // Approval Workflows
    APPROVAL_REQUESTED("Approval Requested", "Action requires approval"),
    APPROVAL_GRANTED("Approval Granted", "Action approved"),
    APPROVAL_REJECTED("Approval Rejected", "Action rejected"),
    APPROVAL_EXPIRED("Approval Expired", "Approval request expired"),
    
    // Compliance & Legal
    COMPLIANCE_CHECK("Compliance Check", "Compliance verification performed"),
    AUDIT_REVIEW("Audit Review", "Record reviewed for audit"),
    LEGAL_HOLD_APPLIED("Legal Hold Applied", "Legal hold applied to record"),
    LEGAL_HOLD_RELEASED("Legal Hold Released", "Legal hold released from record"),
    POLICY_ACCEPTANCE("Policy Acceptance", "Employee accepted policy"),
    
    // Notifications & Communications
    NOTIFICATION_SENT("Notification Sent", "System notification sent"),
    EMAIL_SENT("Email Sent", "Email communication sent"),
    DOCUMENT_SHARED("Document Shared", "Document shared with employee"),
    
    // Error & Security Events
    UNAUTHORIZED_ACCESS("Unauthorized Access", "Unauthorized access attempt"),
    DATA_BREACH_DETECTED("Data Breach Detected", "Potential data breach detected"),
    SUSPICIOUS_ACTIVITY("Suspicious Activity", "Suspicious system activity detected"),
    SYSTEM_ERROR("System Error", "System error occurred"),
    VALIDATION_FAILURE("Validation Failure", "Data validation failed");
    
    private final String displayName;
    private final String description;
    
    AuditAction(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Helper methods to categorize actions
    public boolean isSecurityRelated() {
        return switch (this) {
            case LOGIN_SUCCESSFUL, LOGIN_FAILED, LOGOUT, PASSWORD_CHANGED, PASSWORD_RESET,
                 ACCOUNT_LOCKED, ACCOUNT_UNLOCKED, PERMISSION_GRANTED, PERMISSION_REVOKED,
                 ROLE_ASSIGNED, ROLE_REMOVED, UNAUTHORIZED_ACCESS, DATA_BREACH_DETECTED,
                 SUSPICIOUS_ACTIVITY, SECURITY_LEVEL_CHANGED -> true;
            default -> false;
        };
    }
    
    public boolean isPersonalDataRelated() {
        return switch (this) {
            case PERSONAL_INFO_UPDATED, CONTACT_INFO_UPDATED, ADDRESS_UPDATED,
                 EMERGENCY_CONTACT_UPDATED, BANK_DETAILS_UPDATED, TAX_INFO_UPDATED,
                 GDPR_REQUEST, DATA_ANONYMIZED -> true;
            default -> false;
        };
    }
    
    public boolean isEmploymentRelated() {
        return switch (this) {
            case EMPLOYEE_CREATED, EMPLOYEE_UPDATED, STATUS_CHANGED, CLASSIFICATION_CHANGED,
                 DEPARTMENT_TRANSFER, POSITION_CHANGE, SALARY_ADJUSTMENT, MANAGER_ASSIGNED,
                 MANAGER_CHANGED, CONTRACT_CREATED, CONTRACT_UPDATED, CONTRACT_RENEWED,
                 CONTRACT_TERMINATED -> true;
            default -> false;
        };
    }
    
    public boolean isComplianceRelated() {
        return switch (this) {
            case COMPLIANCE_CHECK, AUDIT_REVIEW, LEGAL_HOLD_APPLIED, LEGAL_HOLD_RELEASED,
                 POLICY_ACCEPTANCE, GDPR_REQUEST, DATA_RETENTION_APPLIED -> true;
            default -> false;
        };
    }
    
    public boolean requiresApproval() {
        return switch (this) {
            case EMPLOYEE_DELETED, EMPLOYEE_TERMINATED, SALARY_ADJUSTMENT, 
                 SECURITY_LEVEL_CHANGED, CONTRACT_TERMINATED, DATA_EXPORT,
                 BULK_UPDATE, GDPR_REQUEST -> true;
            default -> false;
        };
    }
    
    public AuditSeverity getDefaultSeverity() {
        return switch (this) {
            case DATA_BREACH_DETECTED, UNAUTHORIZED_ACCESS, EMPLOYEE_TERMINATED -> AuditSeverity.CRITICAL;
            case SUSPICIOUS_ACTIVITY, SALARY_ADJUSTMENT, SECURITY_LEVEL_CHANGED,
                 CONTRACT_TERMINATED, GDPR_REQUEST -> AuditSeverity.HIGH;
            case EMPLOYEE_CREATED, EMPLOYEE_UPDATED, STATUS_CHANGED, PERMISSION_GRANTED,
                 ROLE_ASSIGNED, PERSONAL_INFO_UPDATED -> AuditSeverity.MEDIUM;
            default -> AuditSeverity.LOW;
        };
    }
    
    public RiskLevel getDefaultRiskLevel() {
        return switch (this) {
            case DATA_BREACH_DETECTED, UNAUTHORIZED_ACCESS -> RiskLevel.CRITICAL;
            case EMPLOYEE_TERMINATED, SUSPICIOUS_ACTIVITY, SALARY_ADJUSTMENT,
                 SECURITY_LEVEL_CHANGED -> RiskLevel.HIGH;
            case EMPLOYEE_CREATED, STATUS_CHANGED, PERMISSION_GRANTED,
                 PERSONAL_INFO_UPDATED -> RiskLevel.MEDIUM;
            default -> RiskLevel.LOW;
        };
    }
}