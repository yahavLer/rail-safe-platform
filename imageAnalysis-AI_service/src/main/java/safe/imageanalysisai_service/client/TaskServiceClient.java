package safe.imageanalysisai_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class TaskServiceClient {

    private final RestClient restClient;

    public TaskServiceClient(@Qualifier("taskRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public CreatedTaskRemoteBoundary createTask(CreateTaskRemoteBoundary input) {
        return restClient.post()
                .uri("/api/tasks")
                .body(input)
                .retrieve()
                .body(CreatedTaskRemoteBoundary.class);
    }

    public record CreateTaskRemoteBoundary(
            UUID orgId,
            UUID riskId,
            UUID assigneeUserId,
            String title,
            String description
    ) {
    }

    public record CreatedTaskRemoteBoundary(
            UUID id,
            String title
    ) {
    }
}