package safe.imageanalysisai_service.boundaries;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.UUID;

public record FinalizeAnalyzedRiskBoundary(
        String title,
        String description,
        String categoryCode,
        @Min(1) @Max(4) Integer severityLevel,
        @Min(1) @Max(4) Integer frequencyLevel,
        UUID divisionId,
        UUID departmentId,
        UUID riskManagerUserId,
        String siteName,
        List<String> suggestedMitigations
) {
}