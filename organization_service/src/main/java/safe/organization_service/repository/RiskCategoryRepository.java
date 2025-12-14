package safe.organization_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import safe.organization_service.entity.RiskCategoryDefinitionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskCategoryRepository extends JpaRepository<RiskCategoryDefinitionEntity, UUID> {
    List<RiskCategoryDefinitionEntity> findByOrganization_IdOrderByDisplayOrderAsc(UUID orgId);
    Optional<RiskCategoryDefinitionEntity> findByOrganization_IdAndCode(UUID orgId, String code);
}