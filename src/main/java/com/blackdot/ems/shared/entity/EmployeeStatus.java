package com.blackdot.ems.shared.entity;

/**
 * Comprehensive Employee Status Enum for Government-Grade Employee Management
 * Suitable for property valuation business with strict HR compliance requirements
 */
public enum EmployeeStatus {
    
    /**
     * ACTIVE - Employee is currently working and has full access to systems
     * - Can access all assigned systems and resources
     * - Eligible for performance reviews and promotions
     * - Full payroll processing
     */
    ACTIVE("Active", "Employee is currently active and working", "#28a745"),
    
    /**
     * INACTIVE - Employee is temporarily not working (unpaid leave, sabbatical)
     * - No system access during inactive period
     * - Position held but no salary processing
     * - Can be reactivated without full onboarding
     */
    INACTIVE("Inactive", "Employee is temporarily inactive", "#6c757d"),
    
    /**
     * SUSPENDED - Employee is under disciplinary action or investigation
     * - Immediate suspension of all system access
     * - Payroll may be suspended based on company policy
     * - Requires HR approval to change status
     * - Detailed audit trail maintained
     */
    SUSPENDED("Suspended", "Employee is suspended pending investigation", "#fd7e14"),
    
    /**
     * BLOCKED - Employee access blocked due to security concerns
     * - All system access immediately revoked
     * - Used for security breaches or policy violations
     * - Requires senior management approval to unblock
     * - Enhanced monitoring when reactivated
     */
    BLOCKED("Blocked", "Employee access blocked for security reasons", "#dc3545"),
    
    /**
     * TERMINATED - Employment has been ended
     * - Permanent status - cannot be reversed
     * - All access permanently revoked
     * - Requires proper exit procedures completion
     * - Historical data preserved for compliance
     */
    TERMINATED("Terminated", "Employment has been terminated", "#343a40"),
    
    /**
     * ON_LEAVE - Employee on approved leave (medical, maternity, etc.)
     * - Limited or no system access based on leave type
     * - Position and benefits maintained
     * - Automatic return date tracking
     * - Different leave types supported
     */
    ON_LEAVE("On Leave", "Employee is on approved leave", "#17a2b8"),
    
    /**
     * PROBATION - New employee under probation period
     * - Limited system access during evaluation
     * - Enhanced monitoring and review processes
     * - Automatic status review at probation end
     * - Special performance tracking
     */
    PROBATION("Probation", "Employee is under probation period", "#ffc107"),
    
    /**
     * PENDING_START - Employee hired but not yet started
     * - Used for tracking pre-start activities
     * - No system access until start date
     * - Onboarding preparation phase
     * - Background check completion tracking
     */
    PENDING_START("Pending Start", "Employee hired but not yet started", "#6f42c1");
    
    private final String displayName;
    private final String description;
    private final String colorCode;
    
    EmployeeStatus(String displayName, String description, String colorCode) {
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getColorCode() {
        return colorCode;
    }
    
    /**
     * Determines if employee has system access based on status
     */
    public boolean hasSystemAccess() {
        return this == ACTIVE || this == PROBATION;
    }
    
    /**
     * Determines if employee is eligible for payroll processing
     */
    public boolean isPayrollEligible() {
        return this == ACTIVE || this == PROBATION || this == ON_LEAVE;
    }
    
    /**
     * Determines if status can be changed (some statuses are permanent)
     */
    public boolean isStatusChangeable() {
        return this != TERMINATED;
    }
    
    /**
     * Gets status categories for filtering and reporting
     */
    public StatusCategory getCategory() {
        switch (this) {
            case ACTIVE:
            case PROBATION:
                return StatusCategory.WORKING;
            case INACTIVE:
            case ON_LEAVE:
                return StatusCategory.TEMPORARY_ABSENCE;
            case SUSPENDED:
            case BLOCKED:
                return StatusCategory.RESTRICTED;
            case TERMINATED:
                return StatusCategory.ENDED;
            case PENDING_START:
                return StatusCategory.PRE_EMPLOYMENT;
            default:
                return StatusCategory.OTHER;
        }
    }
    
    public enum StatusCategory {
        WORKING("Working"),
        TEMPORARY_ABSENCE("Temporary Absence"),
        RESTRICTED("Restricted Access"),
        ENDED("Employment Ended"),
        PRE_EMPLOYMENT("Pre-Employment"),
        OTHER("Other");
        
        private final String displayName;
        
        StatusCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}