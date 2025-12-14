package safe.risk_service.entities;

import jakarta.persistence.*;
import lombok.*;
import safe.risk_service.enums.RiskClassification;
import safe.risk_service.enums.RiskStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Risk entity stored in risk_service DB.
 * - severityLevel and frequencyLevel are fixed numeric levels (1..4).
 * - riskScore and classification are computed (severity * frequency).
 * - "after mitigation" levels are optional and also computed if provided.
 */
@Entity
@Table(name = "risks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RiskEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    // Optional sub-organization mapping
    private UUID divisionId;    // e.g., "חטיבה"
    private UUID departmentId;  // e.g., "אגף"

    // Risk owner (risk manager / department manager userId)
    private UUID riskManagerUserId;

    @Column(nullable = false)
    private String title;

    /**
     * Category code (GH1..GH21). Organization defines these names in organization_service.
     */
    @Column(nullable = false)
    private String categoryCode;

    @Column(nullable = false, length = 4000)
    private String description;

    /**
     * Level 1..4
     */
    @Column(nullable = false)
    private int severityLevel;

    /**
     * Level 1..4
     */
    @Column(nullable = false)
    private int frequencyLevel;

    /**
     * Computed: severityLevel * frequencyLevel
     */
    @Column(nullable = false)
    private int riskScore;

    /**
     * Computed from riskScore
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskClassification classification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskStatus status;

    // Optional meta
    private String location;
    private String notes;

    // After mitigation (optional)
    private Integer severityAfter;
    private Integer frequencyAfter;
    private Integer scoreAfter;

    @Enumerated(EnumType.STRING)
    private RiskClassification classificationAfter;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
