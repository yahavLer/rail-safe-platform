package safe.user_service.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import safe.user_service.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_org_id", columnList = "org_id"),
                @Index(name = "idx_users_external_auth_id", columnList = "external_auth_id")
        }
)
/**
 * Database representation of a user.
 * Holds organizational scope (org/division/department) and role.
 */
public class UserEntity {
    /** Internal system identifier (UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * External auth identifier (optional).
     * Example: Firebase UID or other identity provider subject.
     */
    @Column(name = "external_auth_id", unique = true)
    private String externalAuthId; // למשל Firebase UID (אופציונלי)

    /** The organization this user belongs to. Required. */
    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    /**
     * Division scope (optional).
     * Required for DIVISION_RISK_MANAGER and DEPARTMENT_RISK_MANAGER.
     */
    @Column(name = "division_id")
    private UUID divisionId;

    /**
     * Department scope (optional).
     * Required for DEPARTMENT_RISK_MANAGER.
     */
    @Column(name = "department_id")
    private UUID departmentId;

    /** User authorization role. Required. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /** User first name. */
    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    /** User last name. */
    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    /** Contact email (can also be used as a login identifier, depending on auth model). */
    @Column(nullable = false, length = 120)
    private String email;

    /** Soft status flag for enabling/disabling user access. */
    @Column(nullable = false)
    private boolean active = true;

    /** Creation timestamp (set automatically). */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Update timestamp (set automatically). */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Called automatically before INSERT. */
    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    /** Called automatically before UPDATE. */
    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    // Getters/Setters (kept explicit for clarity)

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
    public Instant getUpdatedAt() { return updatedAt; }
}