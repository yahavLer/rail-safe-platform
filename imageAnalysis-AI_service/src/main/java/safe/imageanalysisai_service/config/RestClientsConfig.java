package safe.imageanalysisai_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientsConfig {

    @Bean
    public RestClient organizationRestClient(
            RestClient.Builder builder,
            @Value("${clients.organization-service.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient riskRestClient(
            RestClient.Builder builder,
            @Value("${clients.risk-service.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient taskRestClient(
            RestClient.Builder builder,
            @Value("${clients.task-service.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient aiProviderRestClient(
            RestClient.Builder builder,
            @Value("${clients.ai-provider.base-url}") String baseUrl,
            @Value("${clients.ai-provider.api-key:}") String apiKey
    ) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}