package safe.imageanalysisai_service.entity;

import jakarta.persistence.*;
import safe.imageanalysisai_service.enums.AnalysisStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ai_risk_analyses")
public class AiRiskAnalysisEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID orgId;

    private UUID divisionId;
    private UUID departmentId;
    private UUID riskManagerUserId;
    private UUID finalizedRiskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private String sourceImageUrl;

    private String aiProvider;
    private String promptVersion;

    private Boolean hazardDetected;
    private Double confidence;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String orgContextJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rawAiResponseJson;

    private String suggestedTitle;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String suggestedDescription;

    private String suggestedCategoryCode;
    private Integer suggestedSeverityLevel;
    private Integer suggestedFrequencyLevel;
    private Integer suggestedScore;
    private String suggestedClassification;
    private String location;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "ai_risk_analysis_mitigations",
            joinColumns = @JoinColumn(name = "analysis_id")
    )
    @Column(name = "mitigation_text", columnDefinition = "TEXT")
    private List<String> suggestedMitigations = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }

    public UUID getDivisionId() { return divisionId; }
    public void setDivisionId(UUID divisionId) { this.divisionId = divisionId; }

    public UUID getDepartmentId() { return departmentId; }
    public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }

    public UUID getRiskManagerUserId() { return riskManagerUserId; }
    public void setRiskManagerUserId(UUID riskManagerUserId) { this.riskManagerUserId = riskManagerUserId; }

    public UUID getFinalizedRiskId() { return finalizedRiskId; }
    public void setFinalizedRiskId(UUID finalizedRiskId) { this.finalizedRiskId = finalizedRiskId; }

    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getSourceImageUrl() { return sourceImageUrl; }
    public void setSourceImageUrl(String sourceImageUrl) { this.sourceImageUrl = sourceImageUrl; }

    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }

    public String getPromptVersion() { return promptVersion; }
    public void setPromptVersion(String promptVersion) { this.promptVersion = promptVersion; }

    public Boolean getHazardDetected() { return hazardDetected; }
    public void setHazardDetected(Boolean hazardDetected) { this.hazardDetected = hazardDetected; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getOrgContextJson() { return orgContextJson; }
    public void setOrgContextJson(String orgContextJson) { this.orgContextJson = orgContextJson; }

    public String getRawAiResponseJson() { return rawAiResponseJson; }
    public void setRawAiResponseJson(String rawAiResponseJson) { this.rawAiResponseJson = rawAiResponseJson; }

    public String getSuggestedTitle() { return suggestedTitle; }
    public void setSuggestedTitle(String suggestedTitle) { this.suggestedTitle = suggestedTitle; }

    public String getSuggestedDescription() { return suggestedDescription; }
    public void setSuggestedDescription(String suggestedDescription) { this.suggestedDescription = suggestedDescription; }

    public String getSuggestedCategoryCode() { return suggestedCategoryCode; }
    public void setSuggestedCategoryCode(String suggestedCategoryCode) { this.suggestedCategoryCode = suggestedCategoryCode; }

    public Integer getSuggestedSeverityLevel() { return suggestedSeverityLevel; }
    public void setSuggestedSeverityLevel(Integer suggestedSeverityLevel) { this.suggestedSeverityLevel = suggestedSeverityLevel; }

    public Integer getSuggestedFrequencyLevel() { return suggestedFrequencyLevel; }
    public void setSuggestedFrequencyLevel(Integer suggestedFrequencyLevel) { this.suggestedFrequencyLevel = suggestedFrequencyLevel; }

    public Integer getSuggestedScore() { return suggestedScore; }
    public void setSuggestedScore(Integer suggestedScore) { this.suggestedScore = suggestedScore; }

    public String getSuggestedClassification() { return suggestedClassification; }
    public void setSuggestedClassification(String suggestedClassification) { this.suggestedClassification = suggestedClassification; }

    public String getlocation() { return location; }
    public void setlocation(String location) { this.location = location; }

    public List<String> getSuggestedMitigations() { return suggestedMitigations; }
    public void setSuggestedMitigations(List<String> suggestedMitigations) {
        this.suggestedMitigations = suggestedMitigations == null
                ? new ArrayList<>()
                : new ArrayList<>(suggestedMitigations);
    }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}