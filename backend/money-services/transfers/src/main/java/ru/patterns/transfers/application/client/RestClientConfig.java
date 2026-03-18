package ru.patterns.transfers.application.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.patterns.shared.constants.UrlConstants;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient currencyClient() {
        return RestClient.builder()
                .baseUrl(UrlConstants.BASE_URL + "/currency/patterns/api" + UrlConstants.CURRENCY_API_VERSION)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
