package com.blackdot.ems.shared.entity;

/**
 * Employee Lifecycle Event Types
 * Defines all possible events in an employee's lifecycle
 */
public enum LifecycleEventType {
    
    // Onboarding Events
    OFFER_EXTENDED("Offer Extended", "Job offer has been extended to candidate"),
    OFFER_ACCEPTED("Offer Accepted", "Candidate has accepted the job offer"),
    BACKGROUND_CHECK_INITIATED("Background Check Started", "Background verification process initiated"),
    BACKGROUND_CHECK_COMPLETED("Background Check Completed", "Background verification completed"),
    CONTRACT_SIGNED("Contract Signed", "Employment contract has been signed"),
    FIRST_DAY_SCHEDULED("First Day Scheduled", "Employee's first day has been scheduled"),
    
    // First Day & Onboarding
    FIRST_DAY("First Day", "Employee's first day at work"),
    ORIENTATION_SCHEDULED("Orientation Scheduled", "New employee orientation scheduled"),
    ORIENTATION_COMPLETED("Orientation Completed", "New employee orientation completed"),
    IT_SETUP_COMPLETED("IT Setup Completed", "IT equipment and access setup completed"),
    WORKSPACE_ASSIGNED("Workspace Assigned", "Physical workspace assigned to employee"),
    BUDDY_ASSIGNED("Buddy Assigned", "Onboarding buddy assigned to new employee"),
    
    // Probation & Training
    PROBATION_STARTED("Probation Started", "Employee probation period started"),
    PROBATION_REVIEW_30("30-Day Probation Review", "30-day probation review"),
    PROBATION_REVIEW_60("60-Day Probation Review", "60-day probation review"),
    PROBATION_REVIEW_90("90-Day Probation Review", "90-day probation review"),
    PROBATION_COMPLETED("Probation Completed", "Employee successfully completed probation"),
    TRAINING_SCHEDULED("Training Scheduled", "Training session scheduled"),
    TRAINING_COMPLETED("Training Completed", "Training session completed"),
    
    // Performance Management
    PERFORMANCE_REVIEW_DUE("Performance Review Due", "Annual/periodic performance review due"),
    PERFORMANCE_REVIEW_SCHEDULED("Performance Review Scheduled", "Performance review meeting scheduled"),
    PERFORMANCE_REVIEW_COMPLETED("Performance Review Completed", "Performance review completed"),
    GOAL_SETTING("Goal Setting", "Annual/quarterly goal setting session"),
    PERFORMANCE_IMPROVEMENT_PLAN("Performance Improvement Plan", "PIP initiated"),
    
    // Career Development
    PROMOTION("Promotion", "Employee promotion"),
    ROLE_CHANGE("Role Change", "Employee role or department change"),
    SALARY_REVIEW("Salary Review", "Salary review and adjustment"),
    TRAINING_NEEDS_ASSESSMENT("Training Needs Assessment", "Assessment of training requirements"),
    
    // Contract & Legal
    CONTRACT_RENEWAL_DUE("Contract Renewal Due", "Employment contract renewal due"),
    CONTRACT_RENEWED("Contract Renewed", "Employment contract renewed"),
    NOTICE_PERIOD_STARTED("Notice Period Started", "Employee notice period commenced"),
    
    // Leave & Absence
    LEAVE_STARTED("Leave Started", "Employee leave period started"),
    LEAVE_ENDED("Leave Ended", "Employee returned from leave"),
    EXTENDED_ABSENCE("Extended Absence", "Extended absence from work"),
    
    // Security & Compliance
    SECURITY_CLEARANCE_REVIEW("Security Clearance Review", "Security clearance review required"),
    SECURITY_TRAINING_DUE("Security Training Due", "Mandatory security training due"),
    COMPLIANCE_CHECK("Compliance Check", "Regulatory compliance check"),
    
    // Disciplinary
    VERBAL_WARNING("Verbal Warning", "Verbal warning issued"),
    WRITTEN_WARNING("Written Warning", "Written warning issued"),
    FINAL_WARNING("Final Warning", "Final warning issued"),
    SUSPENSION("Suspension", "Employee suspension"),
    
    // Exit Process
    RESIGNATION_SUBMITTED("Resignation Submitted", "Employee submitted resignation"),
    EXIT_INTERVIEW_SCHEDULED("Exit Interview Scheduled", "Exit interview scheduled"),
    EXIT_INTERVIEW_COMPLETED("Exit Interview Completed", "Exit interview completed"),
    HANDOVER_INITIATED("Handover Initiated", "Knowledge handover process started"),
    HANDOVER_COMPLETED("Handover Completed", "Knowledge handover completed"),
    IT_EQUIPMENT_RETURNED("IT Equipment Returned", "IT equipment returned"),
    ACCESS_REVOKED("Access Revoked", "System access revoked"),
    FINAL_SETTLEMENT("Final Settlement", "Final salary and benefits settlement"),
    TERMINATION("Termination", "Employment terminated");
    
    private final String displayName;
    private final String description;
    
    LifecycleEventType(String displayName, String description) {
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
     * Get category of the lifecycle event
     */
    public LifecycleCategory getCategory() {
        switch (this) {
            case OFFER_EXTENDED:
            case OFFER_ACCEPTED:
            case BACKGROUND_CHECK_INITIATED:
            case BACKGROUND_CHECK_COMPLETED:
            case CONTRACT_SIGNED:
            case FIRST_DAY_SCHEDULED:
            case FIRST_DAY:
            case ORIENTATION_SCHEDULED:
            case ORIENTATION_COMPLETED:
            case IT_SETUP_COMPLETED:
            case WORKSPACE_ASSIGNED:
            case BUDDY_ASSIGNED:
                return LifecycleCategory.ONBOARDING;
                
            case PROBATION_STARTED:
            case PROBATION_REVIEW_30:
            case PROBATION_REVIEW_60:
            case PROBATION_REVIEW_90:
            case PROBATION_COMPLETED:
            case TRAINING_SCHEDULED:
            case TRAINING_COMPLETED:
                return LifecycleCategory.PROBATION_TRAINING;
                
            case PERFORMANCE_REVIEW_DUE:
            case PERFORMANCE_REVIEW_SCHEDULED:
            case PERFORMANCE_REVIEW_COMPLETED:
            case GOAL_SETTING:
            case PERFORMANCE_IMPROVEMENT_PLAN:
                return LifecycleCategory.PERFORMANCE;
                
            case PROMOTION:
            case ROLE_CHANGE:
            case SALARY_REVIEW:
            case TRAINING_NEEDS_ASSESSMENT:
                return LifecycleCategory.CAREER_DEVELOPMENT;
                
            case CONTRACT_RENEWAL_DUE:
            case CONTRACT_RENEWED:
            case NOTICE_PERIOD_STARTED:
                return LifecycleCategory.CONTRACT_LEGAL;
                
            case LEAVE_STARTED:
            case LEAVE_ENDED:
            case EXTENDED_ABSENCE:
                return LifecycleCategory.LEAVE_ABSENCE;
                
            case SECURITY_CLEARANCE_REVIEW:
            case SECURITY_TRAINING_DUE:
            case COMPLIANCE_CHECK:
                return LifecycleCategory.SECURITY_COMPLIANCE;
                
            case VERBAL_WARNING:
            case WRITTEN_WARNING:
            case FINAL_WARNING:
            case SUSPENSION:
                return LifecycleCategory.DISCIPLINARY;
                
            case RESIGNATION_SUBMITTED:
            case EXIT_INTERVIEW_SCHEDULED:
            case EXIT_INTERVIEW_COMPLETED:
            case HANDOVER_INITIATED:
            case HANDOVER_COMPLETED:
            case IT_EQUIPMENT_RETURNED:
            case ACCESS_REVOKED:
            case FINAL_SETTLEMENT:
            case TERMINATION:
                return LifecycleCategory.EXIT_PROCESS;
                
            default:
                return LifecycleCategory.OTHER;
        }
    }
    
    /**
     * Determines if this event type requires immediate attention
     */
    public boolean isHighPriority() {
        switch (this) {
            case FIRST_DAY:
            case BACKGROUND_CHECK_COMPLETED:
            case PROBATION_REVIEW_90:
            case PERFORMANCE_IMPROVEMENT_PLAN:
            case CONTRACT_RENEWAL_DUE:
            case FINAL_WARNING:
            case SUSPENSION:
            case RESIGNATION_SUBMITTED:
            case TERMINATION:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Get default number of days for due date from event date
     */
    public int getDefaultDueDays() {
        switch (this) {
            case BACKGROUND_CHECK_INITIATED:
                return 10;
            case ORIENTATION_SCHEDULED:
                return 3;
            case PROBATION_REVIEW_30:
                return 30;
            case PROBATION_REVIEW_60:
                return 60;
            case PROBATION_REVIEW_90:
                return 90;
            case PERFORMANCE_REVIEW_DUE:
                return 365;
            case CONTRACT_RENEWAL_DUE:
                return 30;
            case SECURITY_TRAINING_DUE:
                return 14;
            case EXIT_INTERVIEW_SCHEDULED:
                return 7;
            default:
                return 0; // No default due date
        }
    }
}