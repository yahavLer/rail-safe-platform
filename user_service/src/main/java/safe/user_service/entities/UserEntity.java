package safe.user_service.entities;

import jakarta.persistence.*;
import safe.common_domain.BaseEntity;
import safe.user_service.enums.SystemRole;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "app_users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        }
)
public class UserEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;   // בהמשך נוסיף הצפנה / BCrypt

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 50)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SystemRole systemRole = SystemRole.BASIC_USER;

    @Column(nullable = false)
    private boolean active = true;

    // חברות בארגונים שונים
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrganizationMembershipEntity> memberships = new ArrayList<>();

    // ========== getters & setters ==========

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public SystemRole getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<OrganizationMembershipEntity> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<OrganizationMembershipEntity> memberships) {
        this.memberships = memberships;
    }
}
