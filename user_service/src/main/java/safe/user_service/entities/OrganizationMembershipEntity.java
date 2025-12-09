package safe.user_service.entities;

import jakarta.persistence.*;
import safe.common_domain.BaseEntity;
import safe.user_service.enums.OrgRole;

import java.util.UUID;

@Entity
@Table(
        name = "user_org_memberships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_org",
                        columnNames = {"user_id", "organization_id"}
                )
        }
)
public class OrganizationMembershipEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // זה ה-ID של הארגון כמו שמנוהל ב-organization_service (UUID חיצוני)
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "org_role", nullable = false, length = 40)
    private OrgRole orgRole;

    @Column(name = "primary_for_user", nullable = false)
    private boolean primaryForUser = false;

    // ========== getters & setters ==========

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public OrgRole getOrgRole() {
        return orgRole;
    }

    public void setOrgRole(OrgRole orgRole) {
        this.orgRole = orgRole;
    }

    public boolean isPrimaryForUser() {
        return primaryForUser;
    }

    public void setPrimaryForUser(boolean primaryForUser) {
        this.primaryForUser = primaryForUser;
    }
}
