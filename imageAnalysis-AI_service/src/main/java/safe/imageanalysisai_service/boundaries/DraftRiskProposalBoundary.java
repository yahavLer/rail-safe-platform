package safe.imageanalysisai_service.boundaries;

import java.util.List;

public record DraftRiskProposalBoundary(
        String title,
        String description,
        String categoryCode,
        Integer severityLevel,
        Integer frequencyLevel,
        Integer score,
        String classification,
        String siteName,
        List<String> suggestedMitigations
) {
}