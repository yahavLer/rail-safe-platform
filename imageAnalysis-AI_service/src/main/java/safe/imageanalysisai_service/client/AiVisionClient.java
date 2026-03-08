package safe.imageanalysisai_service.client;

import java.util.List;

public interface AiVisionClient {

    AiVisionResult analyze(String imageBase64, String prompt);

    record AiVisionResult(
            boolean hazardDetected,
            String title,
            String description,
            String categoryCode,
            String categoryName,
            Integer severityLevel,
            Integer frequencyLevel,
            List<String> suggestedMitigations,
            Double confidence,
            String rawJson
    ) {
    }
}