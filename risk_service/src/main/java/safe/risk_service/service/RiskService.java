package safe.risk_service.service;


import safe.risk_service.boundaries.*;
import safe.risk_service.enums.RiskClassification;
import safe.risk_service.enums.RiskStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RiskService {
    RiskBoundary create(CreateRiskBoundary input);

    RiskBoundary getById(UUID riskId);

    List<RiskBoundary> list(
            UUID orgId,
            UUID divisionId,
            UUID departmentId,
            UUID riskManagerUserId,
            String categoryCode,
            RiskStatus status,
            RiskClassification classification,
            Integer minScore,
            Integer maxScore
    );

    RiskBoundary update(UUID riskId, UpdateRiskBoundary input);

    RiskBoundary updateStatus(UUID riskId, UpdateRiskStatusBoundary input);

    void delete(UUID riskId);

    // Stats
    Map<RiskStatus, Long> countByStatus(UUID orgId);

    Map<RiskClassification, Long> countByClassification(UUID orgId);
}
