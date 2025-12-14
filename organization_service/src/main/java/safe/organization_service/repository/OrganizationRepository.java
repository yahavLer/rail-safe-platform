package safe.organization_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import safe.organization_service.entity.OrganizationEntity;

import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {}
