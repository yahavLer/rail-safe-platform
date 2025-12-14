package safe.user_service.boundary;


import jakarta.validation.constraints.NotNull;
import safe.user_service.enums.UserRole;
/**
 * Request DTO for updating only the user role.
 */
public class UpdateRoleBoundary {
    @NotNull
    private UserRole role;

    // getters/setters
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
