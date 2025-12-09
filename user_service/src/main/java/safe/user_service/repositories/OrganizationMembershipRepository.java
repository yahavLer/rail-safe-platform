package safe.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import safe.user_service.entities.OrganizationMembershipEntity;
import safe.user_service.entities.UserEntity;
import safe.user_service.enums.OrgRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationMembershipRepository
        extends JpaRepository<OrganizationMembershipEntity, Long> {

    List<OrganizationMembershipEntity> findByUser(UserEntity user);

    List<OrganizationMembershipEntity> findByOrganizationId(Long organizationId);

    List<OrganizationMembershipEntity> findByOrganizationIdAndOrgRole(Long organizationId, OrgRole role);

    Optional<OrganizationMembershipEntity> findByUserAndOrganizationId(UserEntity user, Long organizationId);
}
