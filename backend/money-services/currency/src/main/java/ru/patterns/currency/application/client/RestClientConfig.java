package ru.patterns.currency.application.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${currency.list.url}")
    private String currencyListUrl;

    @Bean
    public RestClient settingsClient() {
        return RestClient.builder()
                .baseUrl(currencyListUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
