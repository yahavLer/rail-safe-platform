package safe.risk_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import safe.risk_service.entities.RiskEntity;

import java.util.UUID;

public interface RiskRepository extends JpaRepository<RiskEntity, UUID>, JpaSpecificationExecutor<RiskEntity> {
}
