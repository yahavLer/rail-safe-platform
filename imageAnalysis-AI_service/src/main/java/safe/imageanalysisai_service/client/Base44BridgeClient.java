package safe.imageanalysisai_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class Base44BridgeClient {

    private final RestClient restClient;

    public Base44BridgeClient(@Qualifier("base44BridgeRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Base44AnalysisResponse analyzeRiskImage(Base44AnalyzeRequest request) {
        Base44AnalysisResponse response = restClient.post()
                .uri("/api/analyze-risk-image")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Base44AnalysisResponse.class);

        if (response == null) {
            throw new IllegalStateException("Base44 bridge returned empty response");
        }

        return response;
    }

    public record Base44AnalyzeRequest(
            String prompt,
            String imageBase64,
            String fileName,
            String contentType
    ) {
    }

    public record Base44AnalysisResponse(
            Boolean hazardDetected,
            String title,
            String description,
            String categoryCode,
            String categoryName,
            Integer severityLevel,
            Integer frequencyLevel,
            List<String> suggestedMitigations,
            Double confidence,
            String fileUrl,
            String rawJson
    ) {
    }
}