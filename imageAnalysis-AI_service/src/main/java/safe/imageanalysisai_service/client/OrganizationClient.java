package safe.imageanalysisai_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class OrganizationClient {

    private final RestClient restClient;

    public OrganizationClient(@Qualifier("organizationRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public OrganizationAiContext getAiContext(UUID orgId) {
        RiskMatrixRemoteBoundary matrix = restClient.get()
                .uri("/api/organizations/{orgId}/risk-matrix", orgId)
                .retrieve()
                .body(RiskMatrixRemoteBoundary.class);

        CategoryRemoteBoundary[] categoriesArray = restClient.get()
                .uri("/api/organizations/{orgId}/categories", orgId)
                .retrieve()
                .body(CategoryRemoteBoundary[].class);

        List<CategoryRemoteBoundary> categories =
                categoriesArray == null ? List.of() : Arrays.asList(categoriesArray);

        return new OrganizationAiContext(
                orgId,
                categories,
                matrix == null ? List.of() : matrix.severityLevels(),
                matrix == null ? List.of() : matrix.frequencyLevels()
        );
    }

    public record OrganizationAiContext(
            UUID orgId,
            List<CategoryRemoteBoundary> categories,
            List<LevelDefinitionRemoteBoundary> severityLevels,
            List<LevelDefinitionRemoteBoundary> frequencyLevels
    ) {
    }

    public record CategoryRemoteBoundary(
            UUID id,
            String code,
            String name,
            String description
    ) {
    }

    public record LevelDefinitionRemoteBoundary(
            Integer level,
            String label,
            String description
    ) {
    }

    public record RiskMatrixRemoteBoundary(
            List<LevelDefinitionRemoteBoundary> severityLevels,
            List<LevelDefinitionRemoteBoundary> frequencyLevels
    ) {
    }
}