package safe.organization_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import safe.organization_service.entity.RiskCategoryDefinitionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskCategoryRepository extends JpaRepository<RiskCategoryDefinitionEntity, UUID> {
    List<RiskCategoryDefinitionEntity> findByOrganization_IdOrderByDisplayOrderAsc(UUID orgId);
    Optional<RiskCategoryDefinitionEntity> findByOrganization_IdAndCode(UUID orgId, String code);
    @Query("select max(c.displayOrder) from RiskCategoryDefinitionEntity c where c.organization.id = :orgId")
    Optional<Integer> findMaxDisplayOrderByOrganizationId(@Param("orgId") UUID orgId);

}