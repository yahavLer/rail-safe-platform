package safe.imageanalysisai_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safe.imageanalysisai_service.entity.AiRiskAnalysisEntity;

import java.util.UUID;

public interface AiRiskAnalysisRepository extends JpaRepository<AiRiskAnalysisEntity, UUID> {
}