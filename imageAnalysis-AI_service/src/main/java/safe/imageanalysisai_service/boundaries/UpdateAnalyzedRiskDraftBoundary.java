package safe.imageanalysisai_service.boundaries;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateAnalyzedRiskDraftBoundary(
        @Size(max = 255) String title,
        String description,
        String categoryCode,
        @Min(1) @Max(4) Integer severityLevel,
        @Min(1) @Max(4) Integer frequencyLevel,
        String location,
        List<String> suggestedMitigations
) {
}