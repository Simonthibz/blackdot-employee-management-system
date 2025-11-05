package com.blackdot.ems.shared.entity;

/**
 * Audit Severity Levels
 * Indicates the importance and impact of audit events
 */
public enum AuditSeverity {
    LOW("Low", "Routine operational actions", "text-info"),
    MEDIUM("Medium", "Important business actions", "text-warning"),
    HIGH("High", "Critical business actions requiring attention", "text-danger"),
    CRITICAL("Critical", "Security incidents or major violations", "text-dark");
    
    private final String displayName;
    private final String description;
    private final String textClass;
    
    AuditSeverity(String displayName, String description, String textClass) {
        this.displayName = displayName;
        this.description = description;
        this.textClass = textClass;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getTextClass() {
        return textClass;
    }
    
    public String getBadgeClass() {
        return switch (this) {
            case LOW -> "bg-info";
            case MEDIUM -> "bg-warning";
            case HIGH -> "bg-danger";
            case CRITICAL -> "bg-dark";
        };
    }
    
    public String getIcon() {
        return switch (this) {
            case LOW -> "bi-info-circle";
            case MEDIUM -> "bi-exclamation-triangle";
            case HIGH -> "bi-exclamation-circle";
            case CRITICAL -> "bi-exclamation-diamond";
        };
    }
}