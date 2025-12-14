package safe.organization_service.boundary;
import java.util.List;

/** Response DTO holding both frequency and severity definitions for an org */
public class RiskMatrixBoundary {
    private List<LevelDefinitionBoundary> frequencyLevels;
    private List<LevelDefinitionBoundary> severityLevels;

    public List<LevelDefinitionBoundary> getFrequencyLevels() { return frequencyLevels; }
    public void setFrequencyLevels(List<LevelDefinitionBoundary> frequencyLevels) { this.frequencyLevels = frequencyLevels; }

    public List<LevelDefinitionBoundary> getSeverityLevels() { return severityLevels; }
    public void setSeverityLevels(List<LevelDefinitionBoundary> severityLevels) { this.severityLevels = severityLevels; }
}