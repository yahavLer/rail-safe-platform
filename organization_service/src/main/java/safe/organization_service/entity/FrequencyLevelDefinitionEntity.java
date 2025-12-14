package safe.organization_service.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "org_frequency_levels",
        uniqueConstraints = @UniqueConstraint(name = "uq_org_frequency_level", columnNames = {"org_id", "level"})
)
/**
 * Defines the organization's description for each fixed frequency level (1..4).
 * The level number and label are fixed; only description is customizable.
 */
public class FrequencyLevelDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Owning organization */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganizationEntity organization;

    /** Fixed numeric level (1..4) */
    @Column(nullable = false)
    private int level;

    /** Fixed label shown to users (e.g., "נדיר", "מדי פעם") */
    @Column(nullable = false, length = 50)
    private String label;

    /** Organization-specific description text (editable) */
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
