package safe.risk_service.boundaries;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Partial update for an existing risk.
 * Any non-null field will be updated.
 */
@Getter @Setter
public class UpdateRiskBoundary {

    private UUID riskManagerUserId;

    private String title;

    @Pattern(regexp = "^GH([1-9]|1\\d|2[0-1])$", message = "categoryCode must match GH1..GH21")
    private String categoryCode;

    private String description;

    @Min(1) @Max(4)
    private Integer severityLevel;

    @Min(1) @Max(4)
    private Integer frequencyLevel;

    private String location;
    private String notes;

    @Min(1) @Max(4)
    private Integer severityAfter;

    @Min(1) @Max(4)
    private Integer frequencyAfter;
}

