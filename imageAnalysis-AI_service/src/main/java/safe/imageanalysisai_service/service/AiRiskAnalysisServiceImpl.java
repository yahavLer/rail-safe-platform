package safe.imageanalysisai_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import safe.imageanalysisai_service.boundaries.AiRiskAnalysisBoundary;
import safe.imageanalysisai_service.boundaries.DraftRiskProposalBoundary;
import safe.imageanalysisai_service.boundaries.FinalizeAnalyzedRiskBoundary;
import safe.imageanalysisai_service.boundaries.UpdateAnalyzedRiskDraftBoundary;
import safe.imageanalysisai_service.client.Base44BridgeClient;
import safe.imageanalysisai_service.client.OrganizationClient;
import safe.imageanalysisai_service.client.RiskServiceClient;
import safe.imageanalysisai_service.client.TaskServiceClient;
import safe.imageanalysisai_service.entity.AiRiskAnalysisEntity;
import safe.imageanalysisai_service.enums.AnalysisStatus;
import safe.imageanalysisai_service.repository.AiRiskAnalysisRepository;
import safe.imageanalysisai_service.util.RiskScoringPolicy;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class AiRiskAnalysisServiceImpl implements AiRiskAnalysisService {

    private final AiRiskAnalysisRepository repository;
    private final OrganizationClient organizationClient;
    private final Base44BridgeClient base44BridgeClient;
    private final RiskServiceClient riskServiceClient;
    private final TaskServiceClient taskServiceClient;
    private final RiskScoringPolicy scoringPolicy;
    private final ObjectMapper objectMapper;

    public AiRiskAnalysisServiceImpl(
            AiRiskAnalysisRepository repository,
            OrganizationClient organizationClient,
            Base44BridgeClient base44BridgeClient,
            RiskServiceClient riskServiceClient,
            TaskServiceClient taskServiceClient,
            RiskScoringPolicy scoringPolicy,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.organizationClient = organizationClient;
        this.base44BridgeClient = base44BridgeClient;
        this.riskServiceClient = riskServiceClient;
        this.taskServiceClient = taskServiceClient;
        this.scoringPolicy = scoringPolicy;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiRiskAnalysisBoundary analyzeDraft(
            UUID orgId,
            UUID divisionId,
            UUID departmentId,
            UUID riskManagerUserId,
            String siteName,
            MultipartFile image
    ) {
        validateImage(image);

        try {
            OrganizationClient.OrganizationAiContext orgContext = organizationClient.getAiContext(orgId);

            String imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());
            String prompt = buildPrompt(orgContext, siteName);

            Base44BridgeClient.Base44AnalysisResponse aiResult =
                    base44BridgeClient.analyzeRiskImage(
                            new Base44BridgeClient.Base44AnalyzeRequest(
                                    prompt,
                                    imageBase64,
                                    image.getOriginalFilename(),
                                    image.getContentType()
                            )
                    );

            Integer severity = scoringPolicy.clampLevel(aiResult.severityLevel());
            Integer frequency = scoringPolicy.clampLevel(aiResult.frequencyLevel());
            Integer score = severity * frequency;
            String classification = scoringPolicy.calculateClassification(score);

            String resolvedCategoryCode = resolveCategoryCode(
                    aiResult.categoryCode(),
                    aiResult.categoryName(),
                    orgContext.categories()
            );

            AiRiskAnalysisEntity entity = new AiRiskAnalysisEntity();
            entity.setOrgId(orgId);
            entity.setDivisionId(divisionId);
            entity.setDepartmentId(departmentId);
            entity.setRiskManagerUserId(riskManagerUserId);
            entity.setOriginalFilename(image.getOriginalFilename());
            entity.setContentType(image.getContentType());
            entity.setFileSize(image.getSize());
            entity.setAiProvider("BASE44");
            entity.setPromptVersion("base44-v1");
            entity.setHazardDetected(aiResult.hazardDetected());
            entity.setConfidence(aiResult.confidence());
            entity.setOrgContextJson(writeJson(orgContext));
            entity.setRawAiResponseJson(aiResult.rawJson());
            entity.setSourceImageUrl(aiResult.fileUrl());
            entity.setSiteName(siteName);

            if (Boolean.TRUE.equals(aiResult.hazardDetected())) {
                entity.setStatus(AnalysisStatus.DRAFT_READY);
                entity.setSuggestedTitle(defaultTitle(aiResult.title(), resolvedCategoryCode, siteName));
                entity.setSuggestedDescription(aiResult.description());
                entity.setSuggestedCategoryCode(resolvedCategoryCode);
                entity.setSuggestedSeverityLevel(severity);
                entity.setSuggestedFrequencyLevel(frequency);
                entity.setSuggestedScore(score);
                entity.setSuggestedClassification(classification);
                entity.setSuggestedMitigations(normalizeMitigations(aiResult.suggestedMitigations()));
            } else {
                entity.setStatus(AnalysisStatus.NO_HAZARD_DETECTED);
                entity.setSuggestedTitle("לא זוהה מפגע מובהק בתמונה");
                entity.setSuggestedDescription(aiResult.description());
                entity.setSuggestedMitigations(List.of());
            }

            repository.save(entity);
            return toBoundary(entity);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to read uploaded image", e);
        }
    }

    @Override
    public AiRiskAnalysisBoundary getById(UUID analysisId) {
        AiRiskAnalysisEntity entity = repository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found: " + analysisId));
        return toBoundary(entity);
    }

    @Override
    public AiRiskAnalysisBoundary updateDraft(UUID analysisId, UpdateAnalyzedRiskDraftBoundary input) {
        AiRiskAnalysisEntity entity = repository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found: " + analysisId));

        if (input.title() != null) entity.setSuggestedTitle(input.title());
        if (input.description() != null) entity.setSuggestedDescription(input.description());
        if (input.categoryCode() != null) entity.setSuggestedCategoryCode(input.categoryCode());
        if (input.siteName() != null) entity.setSiteName(input.siteName());

        if (input.severityLevel() != null) {
            entity.setSuggestedSeverityLevel(scoringPolicy.clampLevel(input.severityLevel()));
        }
        if (input.frequencyLevel() != null) {
            entity.setSuggestedFrequencyLevel(scoringPolicy.clampLevel(input.frequencyLevel()));
        }

        if (input.suggestedMitigations() != null) {
            entity.setSuggestedMitigations(normalizeMitigations(input.suggestedMitigations()));
        }

        Integer severity = entity.getSuggestedSeverityLevel() == null ? 1 : entity.getSuggestedSeverityLevel();
        Integer frequency = entity.getSuggestedFrequencyLevel() == null ? 1 : entity.getSuggestedFrequencyLevel();
        int score = scoringPolicy.calculateScore(severity, frequency);
        String classification = scoringPolicy.calculateClassification(score);

        entity.setSuggestedScore(score);
        entity.setSuggestedClassification(classification);

        repository.save(entity);
        return toBoundary(entity);
    }

    @Override
    public AiRiskAnalysisBoundary finalizeDraft(UUID analysisId, FinalizeAnalyzedRiskBoundary input) {
        AiRiskAnalysisEntity entity = repository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found: " + analysisId));

        if (!Boolean.TRUE.equals(entity.getHazardDetected())) {
            throw new IllegalStateException("Cannot finalize analysis with no detected hazard");
        }

        // merge manual edits from frontend
        if (input.title() != null) entity.setSuggestedTitle(input.title());
        if (input.description() != null) entity.setSuggestedDescription(input.description());
        if (input.categoryCode() != null) entity.setSuggestedCategoryCode(input.categoryCode());
        if (input.siteName() != null) entity.setSiteName(input.siteName());
        if (input.divisionId() != null) entity.setDivisionId(input.divisionId());
        if (input.departmentId() != null) entity.setDepartmentId(input.departmentId());
        if (input.riskManagerUserId() != null) entity.setRiskManagerUserId(input.riskManagerUserId());

        if (input.severityLevel() != null) {
            entity.setSuggestedSeverityLevel(scoringPolicy.clampLevel(input.severityLevel()));
        }
        if (input.frequencyLevel() != null) {
            entity.setSuggestedFrequencyLevel(scoringPolicy.clampLevel(input.frequencyLevel()));
        }
        if (input.suggestedMitigations() != null) {
            entity.setSuggestedMitigations(normalizeMitigations(input.suggestedMitigations()));
        }

        int severity = entity.getSuggestedSeverityLevel() == null ? 1 : entity.getSuggestedSeverityLevel();
        int frequency = entity.getSuggestedFrequencyLevel() == null ? 1 : entity.getSuggestedFrequencyLevel();
        int score = scoringPolicy.calculateScore(severity, frequency);
        String classification = scoringPolicy.calculateClassification(score);

        entity.setSuggestedScore(score);
        entity.setSuggestedClassification(classification);

        RiskServiceClient.CreatedRiskRemoteBoundary createdRisk =
                riskServiceClient.createRisk(
                        new RiskServiceClient.CreateRiskRemoteBoundary(
                                entity.getOrgId(),
                                entity.getDivisionId(),
                                entity.getDepartmentId(),
                                entity.getRiskManagerUserId(),
                                entity.getSuggestedCategoryCode(),
                                entity.getSuggestedTitle(),
                                entity.getSuggestedDescription(),
                                entity.getSuggestedSeverityLevel(),
                                entity.getSuggestedFrequencyLevel(),
                                entity.getSiteName(),
                                entity.getSourceImageUrl()
                        )
                );

        for (String mitigation : entity.getSuggestedMitigations()) {
            taskServiceClient.createTask(
                    new TaskServiceClient.CreateTaskRemoteBoundary(
                            entity.getOrgId(),
                            createdRisk.id(),
                            entity.getRiskManagerUserId(),
                            buildTaskTitle(mitigation),
                            mitigation
                    )
            );
        }

        entity.setFinalizedRiskId(createdRisk.id());
        entity.setStatus(AnalysisStatus.FINALIZED);

        repository.save(entity);
        return toBoundary(entity);
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }
        if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
        if (image.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Maximum image size is 10MB");
        }
    }

    private String buildPrompt(
            OrganizationClient.OrganizationAiContext context,
            String siteName
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
                אתה מומחה בכיר לניהול סיכונים ובטיחות תפעולית.
                נתח את התמונה והצע טיוטת סיכון ארגונית.

                חוקים:
                1. השתמש אך ורק בקטגוריות הסיכון שסופקו לך.
                2. בחר רמת חומרה ורמת תדירות רק מתוך הסקאלה שסופקה.
                3. אם אין מפגע אמיתי וברור - hazardDetected=false.
                4. החזר JSON תקין בלבד.
                5. אל תמציא קטגוריות שלא קיימות בארגון.
                6. תן ניסוח קצר, מקצועי וישים.
                """);

        if (siteName != null && !siteName.isBlank()) {
            sb.append("\nאתר/מיקום משויך: ").append(siteName).append("\n");
        }

        sb.append("\nקטגוריות סיכון בארגון:\n");
        for (OrganizationClient.CategoryRemoteBoundary c : context.categories()) {
            sb.append("- code=").append(c.code())
                    .append(", name=").append(c.name())
                    .append(", description=").append(c.description())
                    .append("\n");
        }

        sb.append("\nרמות חומרה:\n");
        for (OrganizationClient.LevelDefinitionRemoteBoundary s : context.severityLevels()) {
            sb.append("- level=").append(s.level())
                    .append(", label=").append(s.label())
                    .append(", description=").append(s.description())
                    .append("\n");
        }

        sb.append("\nרמות תדירות:\n");
        for (OrganizationClient.LevelDefinitionRemoteBoundary f : context.frequencyLevels()) {
            sb.append("- level=").append(f.level())
                    .append(", label=").append(f.label())
                    .append(", description=").append(f.description())
                    .append("\n");
        }

        sb.append("""
                
                החזר בדיוק JSON במבנה:
                {
                  "hazardDetected": true,
                  "title": "כותרת קצרה לסיכון",
                  "description": "תיאור ברור של המפגע וההשלכות האפשריות",
                  "categoryCode": "CATEGORY_CODE_FROM_LIST",
                  "categoryName": "שם הקטגוריה כפי שמופיע ברשימה",
                  "severityLevel": 1,
                  "frequencyLevel": 1,
                  "suggestedMitigations": [
                    "פעולת מנע 1",
                    "פעולת מנע 2"
                  ],
                  "confidence": 0.0
                }
                """);

        return sb.toString();
    }

    private String resolveCategoryCode(
            String categoryCode,
            String categoryName,
            List<OrganizationClient.CategoryRemoteBoundary> categories
    ) {
        if (categoryCode != null) {
            for (OrganizationClient.CategoryRemoteBoundary c : categories) {
                if (c.code() != null && c.code().equalsIgnoreCase(categoryCode)) {
                    return c.code();
                }
            }
        }

        if (categoryName != null) {
            String normalized = categoryName.trim().toLowerCase(Locale.ROOT);
            for (OrganizationClient.CategoryRemoteBoundary c : categories) {
                if (c.name() != null && c.name().trim().toLowerCase(Locale.ROOT).equals(normalized)) {
                    return c.code();
                }
            }
        }

        return null;
    }

    private String defaultTitle(String aiTitle, String categoryCode, String siteName) {
        if (aiTitle != null && !aiTitle.isBlank()) {
            return aiTitle;
        }
        String base = (categoryCode != null && !categoryCode.isBlank())
                ? "סיכון חדש בקטגוריה " + categoryCode
                : "סיכון חדש מזוהה מתמונה";
        if (siteName != null && !siteName.isBlank()) {
            return base + " - " + siteName;
        }
        return base;
    }

    private List<String> normalizeMitigations(List<String> mitigations) {
        if (mitigations == null) return List.of();
        return mitigations.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private String buildTaskTitle(String mitigation) {
        String clean = mitigation == null ? "Mitigation" : mitigation.trim();
        if (clean.length() <= 80) return clean;
        return clean.substring(0, 80);
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private AiRiskAnalysisBoundary toBoundary(AiRiskAnalysisEntity entity) {
        return new AiRiskAnalysisBoundary(
                entity.getId(),
                entity.getOrgId(),
                entity.getStatus(),
                entity.getHazardDetected(),
                entity.getConfidence(),
                entity.getAiProvider(),
                entity.getFinalizedRiskId(),
                new DraftRiskProposalBoundary(
                        entity.getSuggestedTitle(),
                        entity.getSuggestedDescription(),
                        entity.getSuggestedCategoryCode(),
                        entity.getSuggestedSeverityLevel(),
                        entity.getSuggestedFrequencyLevel(),
                        entity.getSuggestedScore(),
                        entity.getSuggestedClassification(),
                        entity.getSiteName(),
                        entity.getSuggestedMitigations()
                ),
                entity.getSourceImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}