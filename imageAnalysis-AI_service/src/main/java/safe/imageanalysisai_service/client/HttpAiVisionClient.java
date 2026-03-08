package safe.imageanalysisai_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class HttpAiVisionClient implements AiVisionClient {

    private final RestClient restClient;

    public HttpAiVisionClient(@Qualifier("aiProviderRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public AiVisionResult analyze(String imageBase64, String prompt) {
        ProviderRequest request = new ProviderRequest(prompt, imageBase64);

        ProviderResponse response = restClient.post()
                .uri("/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ProviderResponse.class);

        if (response == null || response.result() == null) {
            throw new IllegalStateException("AI provider returned empty response");
        }

        return response.result();
    }

    public record ProviderRequest(
            String prompt,
            String imageBase64
    ) {
    }

    public record ProviderResponse(
            AiVisionResult result
    ) {
    }
}