package ru.patterns.notification.application.infrastructure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${app.security.oauth2.authorization-url}")
    private String authorizationUrl;

    @Value("${app.security.oauth2.token-url}")
    private String tokenUrl;

    @Value("${app.security.oauth2.scope}")
    private String scope;

    @Value("${app.security.oauth2.scope-description}")
    private String scopeDescription;

    @Bean
    public OpenAPI notificationOpenApi() {
        var securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl(authorizationUrl)
                                .tokenUrl(tokenUrl)
                                .scopes(new Scopes().addString(scope, scopeDescription))));

        return new OpenAPI()
                .info(new Info().title("Notification API").version("v1"))
                .components(new Components().addSecuritySchemes("oauth2", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("oauth2", List.of(scope)));
    }
}
