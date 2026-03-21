package ru.patterns.credit.application.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.patterns.shared.constants.UrlConstants;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient accountClient() {
        return RestClient.builder()
                .baseUrl(UrlConstants.BASE_URL + "/core/patterns/api/v2/users")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
