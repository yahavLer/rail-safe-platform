package safe.user_service.boundary;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;
/**
 * Request DTO for assigning organizational scope to a user.
 * Used to move users across org/division/department.
 */
public class AssignOrgUnitBoundary {
    /** Organization is always required */
    @NotNull
    private UUID orgId;

    /** Division/department are optional (but can be required by role rules) */
    private UUID divisionId;
    private UUID departmentId;

    // getters/setters
    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }

    public UUID getDivisionId() { return divisionId; }
    public void setDivisionId(UUID divisionId) { this.divisionId = divisionId; }

    public UUID getDepartmentId() { return departmentId; }
    public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
}