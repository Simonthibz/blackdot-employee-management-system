package com.blackdot.ems.shared.entity;

/**
 * Audit Category Types
 * Groups related audit actions into logical categories for better organization
 */
public enum AuditCategory {
    
    EMPLOYEE_MANAGEMENT("Employee Management", "Actions related to employee record management", "bi-person-fill"),
    AUTHENTICATION("Authentication", "Login, logout, and access control actions", "bi-shield-lock"),
    PERSONAL_DATA("Personal Data", "Changes to personal and sensitive information", "bi-person-badge"),
    EMPLOYMENT_STATUS("Employment Status", "Employment status and classification changes", "bi-briefcase"),
    SECURITY("Security", "Security-related actions and violations", "bi-shield-exclamation"),
    CONTRACTS("Contracts", "Contract management and documentation", "bi-file-text"),
    PERFORMANCE("Performance", "Performance reviews and evaluations", "bi-star"),
    LIFECYCLE("Lifecycle", "Employee lifecycle and workflow events", "bi-diagram-3"),
    COMPLIANCE("Compliance", "Compliance, legal, and regulatory actions", "bi-check-circle"),
    SYSTEM("System", "System operations and administrative actions", "bi-gear"),
    COMMUNICATIONS("Communications", "Notifications and communications", "bi-envelope"),
    DATA_PRIVACY("Data Privacy", "GDPR and data privacy related actions", "bi-lock"),
    APPROVALS("Approvals", "Approval workflow actions", "bi-check-square");
    
    private final String displayName;
    private final String description;
    private final String icon;
    
    AuditCategory(String displayName, String description, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public String getBadgeClass() {
        return switch (this) {
            case EMPLOYEE_MANAGEMENT -> "bg-primary";
            case AUTHENTICATION -> "bg-info";
            case PERSONAL_DATA -> "bg-warning";
            case EMPLOYMENT_STATUS -> "bg-success";
            case SECURITY -> "bg-danger";
            case CONTRACTS -> "bg-secondary";
            case PERFORMANCE -> "bg-warning";
            case LIFECYCLE -> "bg-info";
            case COMPLIANCE -> "bg-success";
            case SYSTEM -> "bg-dark";
            case COMMUNICATIONS -> "bg-primary";
            case DATA_PRIVACY -> "bg-danger";
            case APPROVALS -> "bg-success";
        };
    }
    
    public boolean isHighRiskCategory() {
        return switch (this) {
            case SECURITY, DATA_PRIVACY, COMPLIANCE -> true;
            default -> false;
        };
    }
    
    public boolean requiresRetention() {
        return switch (this) {
            case EMPLOYEE_MANAGEMENT, EMPLOYMENT_STATUS, CONTRACTS, 
                 COMPLIANCE, DATA_PRIVACY, APPROVALS -> true;
            default -> false;
        };
    }
    
    public int getDefaultRetentionDays() {
        return switch (this) {
            case COMPLIANCE, DATA_PRIVACY -> 3650; // 10 years
            case EMPLOYEE_MANAGEMENT, EMPLOYMENT_STATUS, CONTRACTS -> 2555; // 7 years
            case SECURITY, AUTHENTICATION -> 1825; // 5 years
            case PERFORMANCE, APPROVALS -> 1095; // 3 years
            default -> 365; // 1 year
        };
    }
}