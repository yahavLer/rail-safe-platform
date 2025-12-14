package safe.risk_service.enums;
/**
 * RiskEntity classification based on score (severity * frequency).
 * NOTE: In a 4x4 matrix, possible scores are: 1,2,3,4,6,8,9,12,16.
 */
public enum RiskClassification {
    NEGLIGIBLE_GREEN,      // 1-3
    TOLERABLE_YELLOW,      // 4-6
    HIGH_ACTION_ORANGE,    // 8-9
    EXTREME_RED            // 12-16
}
