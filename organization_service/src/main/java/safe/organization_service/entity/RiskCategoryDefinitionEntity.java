package safe.organization_service.entity;


import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "org_risk_categories",
        uniqueConstraints = @UniqueConstraint(name = "uq_org_category_code", columnNames = {"org_id", "code"})
)
/**
 * Organization-specific risk categories. Code is typically GH1..GH21.
 */
public class RiskCategoryDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganizationEntity organization;

    /** Category code like "GH1".."GH21" */
    @Column(nullable = false, length = 10)
    private String code;

    /** Display name like "חשמל", "מים" */
    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean active = true;

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public OrganizationEntity getOrganization() { return organization; }
    public void setOrganization(OrganizationEntity organization) { this.organization = organization; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
