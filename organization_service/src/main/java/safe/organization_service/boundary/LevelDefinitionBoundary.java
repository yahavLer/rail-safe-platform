package safe.organization_service.boundary;


/** Generic DTO for a single level definition (frequency or severity) */
public class LevelDefinitionBoundary {
    private int level;        // 1..4 (fixed)
    private String label;     // fixed label (e.g., "נדיר")
    private String description; // organization-defined text

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}