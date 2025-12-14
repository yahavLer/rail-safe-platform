package safe.user_service.enums;
/**
 * Defines the authorization scope of a user in the organization.
 * These roles are system-wide (not organization-specific).
 */
public enum UserRole {
    /**
     * Can view/manage all risks across the organization
     */
    CHIEF_RISK_MANAGER,

    /**
     * Can view/manage risks only within a specific division
     */
    DIVISION_RISK_MANAGER,

    /**
     * Can view/manage risks only within a specific department
     */
    DEPARTMENT_RISK_MANAGER,

    /**
     * Regular employee (optional role for task assignment, read-only views, etc.)
     */
    EMPLOYEE
}
