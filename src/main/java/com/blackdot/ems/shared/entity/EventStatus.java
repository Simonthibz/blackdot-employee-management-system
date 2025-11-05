package com.blackdot.ems.shared.entity;

/**
 * Event Status for Employee Lifecycle Events
 */
public enum EventStatus {
    
    PENDING("Pending", "Event is scheduled but not yet started"),
    IN_PROGRESS("In Progress", "Event is currently being processed"),
    COMPLETED("Completed", "Event has been successfully completed"),
    OVERDUE("Overdue", "Event is past its due date"),
    CANCELLED("Cancelled", "Event has been cancelled"),
    POSTPONED("Postponed", "Event has been postponed to a later date"),
    FAILED("Failed", "Event could not be completed successfully"),
    NOT_APPLICABLE("Not Applicable", "Event is not applicable for this employee");
    
    private final String displayName;
    private final String description;
    
    EventStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Determines if this status indicates the event is still active/actionable
     */
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == OVERDUE || this == POSTPONED;
    }
    
    /**
     * Determines if this status indicates completion (successful or otherwise)
     */
    public boolean isCompleted() {
        return this == COMPLETED || this == CANCELLED || this == FAILED || this == NOT_APPLICABLE;
    }
    
    /**
     * Determines if this status requires attention
     */
    public boolean requiresAttention() {
        return this == OVERDUE || this == FAILED || this == IN_PROGRESS;
    }
    
    /**
     * Get CSS class for status badge styling
     */
    public String getBadgeClass() {
        switch (this) {
            case PENDING:
                return "badge-warning";
            case IN_PROGRESS:
                return "badge-info";
            case COMPLETED:
                return "badge-success";
            case OVERDUE:
                return "badge-danger";
            case CANCELLED:
                return "badge-secondary";
            case POSTPONED:
                return "badge-warning";
            case FAILED:
                return "badge-danger";
            case NOT_APPLICABLE:
                return "badge-light";
            default:
                return "badge-secondary";
        }
    }
    
    /**
     * Get icon for status display
     */
    public String getIcon() {
        switch (this) {
            case PENDING:
                return "clock";
            case IN_PROGRESS:
                return "spinner";
            case COMPLETED:
                return "check-circle";
            case OVERDUE:
                return "exclamation-triangle";
            case CANCELLED:
                return "times-circle";
            case POSTPONED:
                return "pause-circle";
            case FAILED:
                return "times";
            case NOT_APPLICABLE:
                return "minus-circle";
            default:
                return "question-circle";
        }
    }
}