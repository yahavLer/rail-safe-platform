package safe.risk_service.boundaries;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import safe.risk_service.enums.RiskStatus;

/**
 * Request body for changing a risk status.
 */
@Getter @Setter
public class UpdateRiskStatusBoundary {
    @NotNull
    private RiskStatus status;
}
