package ru.patterns.gateway.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import ru.patterns.shared.model.response.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UnstableFilter implements GlobalFilter, Ordered {

    @Value("${unstable.enabled}")
    private Boolean enabled;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        String uri = exchange.getRequest().getURI().getPath();

        if (!enabled || !isServiceUnstable(uri)) {
            return chain.filter(exchange);
        }

        int minute = LocalDateTime.now().getMinute();
        double errorProbability = isEven(minute) ? 0.7 : 0.3;

        if (ThreadLocalRandom.current().nextDouble() < errorProbability) {
            return writeErrorResponse(exchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    private boolean isEven(int number) {
        return number % 2 == 0;
    }

    private boolean isServiceUnstable(String uri) {
        return uri.startsWith("/core")
                || uri.startsWith("/users")
                || uri.startsWith("/credit");
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            byte[] responseBody = mapper.writeValueAsBytes(new ErrorResponse(500, "Ошибка сервера!"));
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBody);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (IOException exception) {
            return exchange.getResponse().setComplete();
        }
    }
}
