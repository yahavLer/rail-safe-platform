package safe.imageanalysisai_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientsConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

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
    public RestClient base44BridgeRestClient(
            RestClient.Builder builder,
            @Value("${clients.base44-bridge.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}