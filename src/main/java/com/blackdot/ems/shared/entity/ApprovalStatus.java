package com.blackdot.ems.shared.entity;

/**
 * Approval Status for audit events requiring approval
 */
public enum ApprovalStatus {
    NOT_REQUIRED("Not Required", "No approval required", "bg-light text-dark"),
    PENDING("Pending", "Awaiting approval", "bg-warning"),
    APPROVED("Approved", "Action approved", "bg-success"),
    REJECTED("Rejected", "Action rejected", "bg-danger"),
    EXPIRED("Expired", "Approval request expired", "bg-secondary");
    
    private final String displayName;
    private final String description;
    private final String badgeClass;
    
    ApprovalStatus(String displayName, String description, String badgeClass) {
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
            case NOT_REQUIRED -> "bi-dash-circle";
            case PENDING -> "bi-clock";
            case APPROVED -> "bi-check-circle";
            case REJECTED -> "bi-x-circle";
            case EXPIRED -> "bi-hourglass";
        };
    }
    
    public boolean isActive() {
        return this == PENDING;
    }
    
    public boolean isCompleted() {
        return this == APPROVED || this == REJECTED || this == EXPIRED;
    }
    
    public boolean requiresAction() {
        return this == PENDING;
    }
}