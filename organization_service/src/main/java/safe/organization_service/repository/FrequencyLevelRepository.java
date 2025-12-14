package safe.organization_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safe.organization_service.entity.FrequencyLevelDefinitionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface FrequencyLevelRepository extends JpaRepository<FrequencyLevelDefinitionEntity, UUID> {
    List<FrequencyLevelDefinitionEntity> findByOrganization_IdOrderByLevelAsc(UUID orgId);
    Optional<FrequencyLevelDefinitionEntity> findByOrganization_IdAndLevel(UUID orgId, int level);
}