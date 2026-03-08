package safe.imageanalysisai_service.boundaries;

import safe.imageanalysisai_service.enums.AnalysisStatus;

import java.time.Instant;
import java.util.UUID;

public record AiRiskAnalysisBoundary(
        UUID id,
        UUID orgId,
        AnalysisStatus status,
        Boolean hazardDetected,
        Double confidence,
        String aiProvider,
        UUID finalizedRiskId,
        DraftRiskProposalBoundary draft,
        String sourceImageUrl,
        Instant createdAt,
        Instant updatedAt
) {
}