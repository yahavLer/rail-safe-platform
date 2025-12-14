package safe.organization_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safe.organization_service.entity.SeverityLevelDefinitionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeverityLevelRepository extends JpaRepository<SeverityLevelDefinitionEntity, UUID> {
    List<SeverityLevelDefinitionEntity> findByOrganization_IdOrderByLevelAsc(UUID orgId);
    Optional<SeverityLevelDefinitionEntity> findByOrganization_IdAndLevel(UUID orgId, int level);
}

