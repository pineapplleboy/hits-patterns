package ru.patterns.shared.utility;

import lombok.experimental.UtilityClass;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.function.Supplier;

@UtilityClass
public final class RestClientRetryUtility {

    private final RetryTemplate RETRY_TEMPLATE = RetryTemplate.builder()
            .maxAttempts(3)
            .exponentialBackoff(300, 2.0, 5000)
            .retryOn(ResourceAccessException.class)
            .retryOn(HttpServerErrorException.class)
            .build();

    public <T> T execute(Supplier<T> action) {
        return RETRY_TEMPLATE.execute(context -> action.get());
    }
}
