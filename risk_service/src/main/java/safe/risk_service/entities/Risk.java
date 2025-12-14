package safe.risk_service.entities;

import jakarta.persistence.*;
import lombok.*;
import safe.common_domain.BaseEntity;
import safe.common_domain.enums.RiskSeverity;
import safe.common_domain.enums.RiskLikelihood;
import safe.common_domain.enums.RiskAcceptanceLevel;
import safe.risk_service.enums.RiskStatus;

@Entity
@Table(name = "risks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Risk extends BaseEntity {

    @Column(nullable = false)
    private Long organizationId;     // כרגע רכבת ישראל, בעתיד ארגונים אחרים

    @Column(nullable = true)
    private Long siteId;             // תחנה / מתחם, אפשר גם String בשלב ראשון

    @Column(nullable = false, length = 200)
    private String title;            // כותרת הסיכון

    @Column(columnDefinition = "TEXT")
    private String description;      // תיאור מלא

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLikelihood likelihood;

    @Column(nullable = false)
    private Integer riskScore;       // severity.rating * likelihood.rating

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskAcceptanceLevel acceptanceLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskStatus status;       // OPEN / MITIGATION_PLANNED / IN_PROGRESS / CLOSED

    @Column(nullable = true)
    private Long parentRiskId;       // כדי לתמוך בסיכון אב/בן/נכד

    @Column(nullable = true)
    private Long ownerUserId;        // בעל הסיכון (מנהל בטיחות / אחראי יחידה)
}
