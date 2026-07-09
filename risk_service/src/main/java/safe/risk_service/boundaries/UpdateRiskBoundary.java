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

    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9_-]{0,9}$", message = "categoryCode must be 1-10 chars and contain only letters/digits/_/-")
    private String categoryCode;

    private String description;

    @Min(1) @Max(4)
    private Integer severityLevel;

    @Min(1) @Max(4)
    private Integer frequencyLevel;

    private String location;
    private String notes;
    private String sourceImageUrl;

    @Min(1) @Max(4)
    private Integer severityAfter;

    @Min(1) @Max(4)
    private Integer frequencyAfter;
}

