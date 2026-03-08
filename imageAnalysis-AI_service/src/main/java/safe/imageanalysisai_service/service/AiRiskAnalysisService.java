package safe.imageanalysisai_service.service;

import org.springframework.web.multipart.MultipartFile;
import safe.imageanalysisai_service.boundaries.AiRiskAnalysisBoundary;
import safe.imageanalysisai_service.boundaries.FinalizeAnalyzedRiskBoundary;
import safe.imageanalysisai_service.boundaries.UpdateAnalyzedRiskDraftBoundary;

import java.util.UUID;

public interface AiRiskAnalysisService {

    AiRiskAnalysisBoundary analyzeDraft(
            UUID orgId,
            UUID divisionId,
            UUID departmentId,
            UUID riskManagerUserId,
            String siteName,
            MultipartFile image
    );

    AiRiskAnalysisBoundary getById(UUID analysisId);

    AiRiskAnalysisBoundary updateDraft(UUID analysisId, UpdateAnalyzedRiskDraftBoundary input);

    AiRiskAnalysisBoundary finalizeDraft(UUID analysisId, FinalizeAnalyzedRiskBoundary input);
}