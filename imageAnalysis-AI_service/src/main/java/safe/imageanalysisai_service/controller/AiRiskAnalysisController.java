package safe.imageanalysisai_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import safe.imageanalysisai_service.boundaries.AiRiskAnalysisBoundary;
import safe.imageanalysisai_service.boundaries.FinalizeAnalyzedRiskBoundary;
import safe.imageanalysisai_service.boundaries.UpdateAnalyzedRiskDraftBoundary;
import safe.imageanalysisai_service.service.AiRiskAnalysisService;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai-risk-analyses")
public class AiRiskAnalysisController {

    private final AiRiskAnalysisService service;

    public AiRiskAnalysisController(AiRiskAnalysisService service) {
        this.service = service;
    }

    @PostMapping(
            value = "/analyze-draft",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public AiRiskAnalysisBoundary analyzeDraft(
            @RequestParam UUID orgId,
            @RequestParam(required = false) UUID divisionId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID riskManagerUserId,
            @RequestParam(required = false) String siteName,
            @RequestPart("image") MultipartFile image
    ) {
        return service.analyzeDraft(orgId, divisionId, departmentId, riskManagerUserId, siteName, image);
    }

    @GetMapping("/{analysisId}")
    public AiRiskAnalysisBoundary getById(@PathVariable UUID analysisId) {
        return service.getById(analysisId);
    }

    @PatchMapping("/{analysisId}/draft")
    public AiRiskAnalysisBoundary updateDraft(
            @PathVariable UUID analysisId,
            @Valid @RequestBody UpdateAnalyzedRiskDraftBoundary input
    ) {
        return service.updateDraft(analysisId, input);
    }

    @PostMapping("/{analysisId}/finalize")
    public AiRiskAnalysisBoundary finalizeDraft(
            @PathVariable UUID analysisId,
            @Valid @RequestBody FinalizeAnalyzedRiskBoundary input
    ) {
        return service.finalizeDraft(analysisId, input);
    }
}