package safe.risk_service.boundaries;

import lombok.Getter;
import lombok.Setter;
import safe.risk_service.enums.RiskClassification;
import safe.risk_service.enums.RiskStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for risk.
 */
@Getter @Setter
public class RiskBoundary {
    private UUID id;
    private UUID organizationId;
    private UUID divisionId;
    private UUID departmentId;
    private UUID riskManagerUserId;

    private String title;
    private String categoryCode;
    private String description;

    private int severityLevel;
    private int frequencyLevel;

    private int riskScore;
    private RiskClassification classification;

    private RiskStatus status;

    private String location;
    private String notes;

    private Integer severityAfter;
    private Integer frequencyAfter;
    private Integer scoreAfter;
    private RiskClassification classificationAfter;

    private Instant createdAt;
    private Instant updatedAt;
}
