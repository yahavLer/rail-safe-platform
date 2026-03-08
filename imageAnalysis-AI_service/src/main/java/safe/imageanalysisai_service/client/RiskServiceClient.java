package safe.imageanalysisai_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class RiskServiceClient {

    private final RestClient restClient;

    public RiskServiceClient(@Qualifier("riskRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public CreatedRiskRemoteBoundary createRisk(CreateRiskRemoteBoundary input) {
        return restClient.post()
                .uri("/api/risks")
                .body(input)
                .retrieve()
                .body(CreatedRiskRemoteBoundary.class);
    }

    public record CreateRiskRemoteBoundary(
            UUID orgId,
            UUID divisionId,
            UUID departmentId,
            UUID riskManagerUserId,
            String categoryCode,
            String title,
            String description,
            Integer severityLevel,
            Integer frequencyLevel,
            String siteName,
            String sourceImageUrl
    ) {
    }

    public record CreatedRiskRemoteBoundary(
            UUID id,
            String title
    ) {
    }
}