package safe.user_service.boundary;

import safe.user_service.enums.UserRole;


import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO sent back to API clients.
 * Keeps internal DB details hidden while exposing required fields.
 */
public class UserBoundary {
    private UUID id;
    private String externalAuthId;

    private UUID orgId;
    private UUID divisionId;
    private UUID departmentId;

    private UserRole role;

    private String firstName;
    private String lastName;
    private String email;

    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}