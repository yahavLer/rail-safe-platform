package safe.imageanalysisai_service.util;

import org.springframework.stereotype.Component;

@Component
public class RiskScoringPolicy {

    public int calculateScore(int severityLevel, int frequencyLevel) {
        return severityLevel * frequencyLevel;
    }

    public String calculateClassification(int score) {
        if (score >= 12) return "EXTREME_RED";
        if (score >= 8) return "HIGH_ACTION_ORANGE";
        if (score >= 4) return "MEDIUM_YELLOW";
        return "LOW_GREEN";
    }

    public int clampLevel(Integer level) {
        if (level == null) return 1;
        if (level < 1) return 1;
        return Math.min(level, 4);
    }
}