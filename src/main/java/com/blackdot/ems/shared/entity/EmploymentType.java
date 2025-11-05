package com.blackdot.ems.shared.entity;

/**
 * Employment Type Classification for Property Valuation Business
 * Defines different types of employment contracts and arrangements
 */
public enum EmploymentType {
    
    /**
     * PERMANENT - Full-time permanent employee
     * - Complete benefits package
     * - Job security and career progression
     * - Standard notice periods
     * - Annual performance reviews
     */
    PERMANENT("Permanent", "Full-time permanent employee", true),
    
    /**
     * CONTRACT - Fixed-term contract employee
     * - Specific contract duration
     * - Project-based employment
     * - Limited benefits based on contract
     * - Contract renewal required
     */
    CONTRACT("Contract", "Fixed-term contract employee", false),
    
    /**
     * TEMPORARY - Short-term temporary employee
     * - Usually for specific needs or peak periods
     * - Minimum benefits
     * - Easy termination process
     * - Often through staffing agencies
     */
    TEMPORARY("Temporary", "Short-term temporary employee", false),
    
    /**
     * CONSULTANT - Independent consultant or advisor
     * - Professional services contract
     * - Specialized expertise
     * - No employee benefits
     * - Invoice-based payment
     */
    CONSULTANT("Consultant", "Independent consultant or advisor", false),
    
    /**
     * INTERN - Student or graduate intern
     * - Training and development focus
     * - Limited responsibilities
     * - Educational institution partnership
     * - Minimal or no salary
     */
    INTERN("Intern", "Student or graduate intern", false),
    
    /**
     * PART_TIME - Part-time employee
     * - Reduced working hours
     * - Pro-rated benefits
     * - Flexible schedule arrangements
     * - Can be permanent or temporary
     */
    PART_TIME("Part-Time", "Part-time employee", true),
    
    /**
     * SEASONAL - Seasonal employee
     * - Employment during specific seasons
     * - Property valuation peak periods
     * - Limited duration contracts
     * - Recurring employment possible
     */
    SEASONAL("Seasonal", "Seasonal employee for peak periods", false);
    
    private final String displayName;
    private final String description;
    private final boolean eligibleForBenefits;
    
    EmploymentType(String displayName, String description, boolean eligibleForBenefits) {
        this.displayName = displayName;
        this.description = description;
        this.eligibleForBenefits = eligibleForBenefits;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isEligibleForBenefits() {
        return eligibleForBenefits;
    }
    
    /**
     * Determines if employment type requires formal contract
     */
    public boolean requiresFormalContract() {
        return this == CONTRACT || this == CONSULTANT || this == SEASONAL;
    }
    
    /**
     * Gets standard probation period in months based on employment type
     */
    public int getStandardProbationMonths() {
        switch (this) {
            case PERMANENT:
                return 6;
            case CONTRACT:
                return 3;
            case PART_TIME:
                return 3;
            case CONSULTANT:
            case INTERN:
            case TEMPORARY:
            case SEASONAL:
            default:
                return 0;
        }
    }
}