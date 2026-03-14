package ru.patterns.account.application.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.patterns.shared.constants.UrlConstants;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient settingsClient() {
        return RestClient.builder()
                .baseUrl(UrlConstants.BASE_URL + "/settings/patterns/api" + UrlConstants.SETTINGS_API_VERSION + "/user/setting")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
