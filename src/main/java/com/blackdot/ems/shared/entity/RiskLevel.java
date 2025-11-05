package com.blackdot.ems.shared.entity;

/**
 * Risk Levels for audit events
 * Assesses the risk impact of actions on business operations
 */
public enum RiskLevel {
    LOW("Low Risk", "Minimal impact on business operations", "bg-success"),
    MEDIUM("Medium Risk", "Moderate impact requiring monitoring", "bg-warning"),
    HIGH("High Risk", "Significant impact requiring immediate attention", "bg-danger"),
    CRITICAL("Critical Risk", "Severe impact threatening business continuity", "bg-dark");
    
    private final String displayName;
    private final String description;
    private final String badgeClass;
    
    RiskLevel(String displayName, String description, String badgeClass) {
        this.displayName = displayName;
        this.description = description;
        this.badgeClass = badgeClass;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getBadgeClass() {
        return badgeClass;
    }
    
    public String getIcon() {
        return switch (this) {
            case LOW -> "bi-check-circle";
            case MEDIUM -> "bi-exclamation-triangle";
            case HIGH -> "bi-exclamation-circle";
            case CRITICAL -> "bi-x-circle";
        };
    }
    
    public boolean requiresImmediateAttention() {
        return this == HIGH || this == CRITICAL;
    }
    
    public boolean requiresApproval() {
        return this == HIGH || this == CRITICAL;
    }
}