package safe.organization_service.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "org_severity_levels",
        uniqueConstraints = @UniqueConstraint(name = "uq_org_severity_level", columnNames = {"org_id", "level"})
)
/**
 * Defines the organization's description for each fixed severity level (1..4).
 * The level number and label are fixed; only description is customizable.
 */
public class SeverityLevelDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganizationEntity organization;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(length = 2000)
    private String description;

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public OrganizationEntity getOrganization() { return organization; }
    public void setOrganization(OrganizationEntity organization) { this.organization = organization; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
