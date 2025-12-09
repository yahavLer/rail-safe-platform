package safe.user_service.dto;
import safe.user_service.enums.OrgRole;

import java.util.UUID;
public class OrganizationMembershipDto {
    private Long organizationId;
    private OrgRole orgRole;
    private boolean primaryForUser;

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
