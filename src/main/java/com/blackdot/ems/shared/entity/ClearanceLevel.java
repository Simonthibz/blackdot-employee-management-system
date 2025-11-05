package com.blackdot.ems.shared.entity;

/**
 * Security Clearance Level for Property Valuation Business
 * Defines access levels for sensitive financial and property data
 */
public enum ClearanceLevel {
    
    /**
     * PUBLIC - Basic access level for general employees
     * - Access to public information only
     * - No access to sensitive financial data
     * - Standard employee permissions
     */
    PUBLIC("Public", "Basic access level", 1, "#6c757d"),
    
    /**
     * INTERNAL - Access to internal company information
     * - Internal reports and documents
     * - Basic client information (non-sensitive)
     * - Standard operational data
     */
    INTERNAL("Internal", "Access to internal company information", 2, "#17a2b8"),
    
    /**
     * CONFIDENTIAL - Access to confidential business data
     * - Client financial information
     * - Property valuation reports
     * - Business strategy documents
     * - Requires background check
     */
    CONFIDENTIAL("Confidential", "Access to confidential business data", 3, "#ffc107"),
    
    /**
     * RESTRICTED - Access to highly sensitive information
     * - High-value property portfolios
     * - Strategic client relationships
     * - Financial forecasting models
     * - Requires enhanced background verification
     */
    RESTRICTED("Restricted", "Access to highly sensitive information", 4, "#fd7e14"),
    
    /**
     * SECRET - Access to most sensitive business data
     * - Major client contracts and negotiations
     * - Merger and acquisition information
     * - Executive compensation data
     * - Requires comprehensive security clearance
     */
    SECRET("Secret", "Access to most sensitive business data", 5, "#dc3545"),
    
    /**
     * EXECUTIVE - Highest level access for senior leadership
     * - All company information
     * - Board-level strategic documents
     * - Regulatory compliance data
     * - Legal and audit information
     */
    EXECUTIVE("Executive", "Highest level access for senior leadership", 6, "#343a40");
    
    private final String displayName;
    private final String description;
    private final int hierarchyLevel;
    private final String colorCode;
    
    ClearanceLevel(String displayName, String description, int hierarchyLevel, String colorCode) {
        this.displayName = displayName;
        this.description = description;
        this.hierarchyLevel = hierarchyLevel;
        this.colorCode = colorCode;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }
    
    public String getColorCode() {
        return colorCode;
    }
    
    /**
     * Determines if this clearance level can access data at the specified level
     */
    public boolean canAccess(ClearanceLevel requiredLevel) {
        return this.hierarchyLevel >= requiredLevel.hierarchyLevel;
    }
    
    /**
     * Gets the review period for clearance renewal (in months)
     */
    public int getReviewPeriodMonths() {
        switch (this) {
            case PUBLIC:
            case INTERNAL:
                return 24; // 2 years
            case CONFIDENTIAL:
                return 18; // 1.5 years
            case RESTRICTED:
                return 12; // 1 year
            case SECRET:
            case EXECUTIVE:
                return 6; // 6 months
            default:
                return 12;
        }
    }
    
    /**
     * Determines if background check is required for this clearance level
     */
    public boolean requiresBackgroundCheck() {
        return this.hierarchyLevel >= CONFIDENTIAL.hierarchyLevel;
    }
    
    /**
     * Gets minimum employment duration required before granting this clearance
     */
    public int getMinimumEmploymentMonths() {
        switch (this) {
            case PUBLIC:
            case INTERNAL:
                return 0;
            case CONFIDENTIAL:
                return 3;
            case RESTRICTED:
                return 6;
            case SECRET:
                return 12;
            case EXECUTIVE:
                return 24;
            default:
                return 0;
        }
    }
}