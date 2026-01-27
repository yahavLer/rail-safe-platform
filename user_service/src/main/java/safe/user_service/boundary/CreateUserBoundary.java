package safe.user_service.boundary;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import safe.user_service.enums.UserRole;

import java.util.UUID;


/**
 * Request DTO for creating a new user.
 * Validated by Spring (@Valid in controller).
 */
public class CreateUserBoundary {

    /** Optional external identity (e.g., Firebase UID). */
    private String externalAuthId;

    /** Required organization ID. */
    @NotNull
    private UUID orgId;

    /** Optional division scope (depends on role). */
    private UUID divisionId;

    /** Optional department scope (depends on role). */
    private UUID departmentId;

    /** Required role. */
    @NotNull
    private UserRole role;

    /** Required first name. */
    @NotBlank
    private String firstName;

    /** Required last name. */
    @NotBlank
    private String lastName;

    /** Required email. */
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    // getters/setters
    public String getExternalAuthId() { return externalAuthId; }
    public void setExternalAuthId(String externalAuthId) { this.externalAuthId = externalAuthId; }

    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }

    public UUID getDivisionId() { return divisionId; }
    public void setDivisionId(UUID divisionId) { this.divisionId = divisionId; }

    public UUID getDepartmentId() { return departmentId; }
    public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}