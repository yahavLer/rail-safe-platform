package safe.risk_service.repository;


import org.springframework.data.jpa.domain.Specification;
import safe.risk_service.entities.RiskEntity;
import safe.risk_service.enums.RiskClassification;
import safe.risk_service.enums.RiskStatus;

import java.util.UUID;

/**
 * Dynamic filters for GET /api/risks
 */
public class RiskSpecifications {

    public static Specification<RiskEntity> orgId(UUID orgId) {
        return (root, query, cb) -> cb.equal(root.get("organizationId"), orgId);
    }

    public static Specification<RiskEntity> status(RiskStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<RiskEntity> classification(RiskClassification c) {
        return (root, query, cb) -> cb.equal(root.get("classification"), c);
    }

    public static Specification<RiskEntity> categoryCode(String code) {
        return (root, query, cb) -> cb.equal(root.get("categoryCode"), code);
    }

    public static Specification<RiskEntity> divisionId(UUID divisionId) {
        return (root, query, cb) -> cb.equal(root.get("divisionId"), divisionId);
    }

    public static Specification<RiskEntity> departmentId(UUID departmentId) {
        return (root, query, cb) -> cb.equal(root.get("departmentId"), departmentId);
    }

    public static Specification<RiskEntity> riskManagerUserId(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("riskManagerUserId"), userId);
    }

    public static Specification<RiskEntity> scoreBetween(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min != null && max != null) return cb.between(root.get("riskScore"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("riskScore"), min);
            if (max != null) return cb.lessThanOrEqualTo(root.get("riskScore"), max);
            return cb.conjunction();
        };
    }
}

