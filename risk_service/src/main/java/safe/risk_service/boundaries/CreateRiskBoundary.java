package safe.risk_service.boundaries;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Request body for creating a new risk.
 */
@Getter @Setter
public class CreateRiskBoundary {

    @NotNull
    private UUID organizationId;

    private UUID divisionId;
    private UUID departmentId;

    private UUID riskManagerUserId;

    @NotBlank
    private String title;

    @NotBlank
    @Pattern(regexp = "^GH([1-9]|1\\d|2[0-1])$", message = "categoryCode must match GH1..GH21")
    private String categoryCode;

    @NotBlank
    private String description;

    @Min(1) @Max(4)
    private int severityLevel;

    @Min(1) @Max(4)
    private int frequencyLevel;

    private String location;
    private String notes;

    // Optional after mitigation estimate
    @Min(1) @Max(4)
    private Integer severityAfter;

    @Min(1) @Max(4)
    private Integer frequencyAfter;
}
