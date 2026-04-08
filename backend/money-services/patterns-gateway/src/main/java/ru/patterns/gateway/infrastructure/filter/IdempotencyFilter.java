package ru.patterns.gateway.infrastructure.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import ru.patterns.gateway.application.common.IdempotencyPayload;
import ru.patterns.gateway.application.common.IdempotencyStatus;
import ru.patterns.gateway.application.common.ResponseData;
import ru.patterns.gateway.application.service.IdempotencyService;
import ru.patterns.gateway.domain.IdempotencyRequest;
import ru.patterns.gateway.infrastructure.capture.CapturingResponse;
import ru.patterns.shared.model.response.ErrorResponse;
import ru.patterns.shared.model.response.UuidResponseModel;
import ru.patterns.shared.utility.JwtAuthUtility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdempotencyFilter implements GlobalFilter, Ordered {

    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!isIdempotentMethod(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String idempotencyKey = exchange.getRequest().getHeaders().getFirst(IDEMPOTENCY_HEADER);
        if (!StringUtils.hasText(idempotencyKey)) {
            idempotencyKey = UUID.randomUUID().toString();
        }
        String finalIdempotencyKey = idempotencyKey;

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .defaultIfEmpty(bufferFactory.wrap(new byte[0]))
                .flatMap(requestBodyBuffer -> {
                    byte[] requestBodyBytes = new byte[requestBodyBuffer.readableByteCount()];
                    requestBodyBuffer.read(requestBodyBytes);
                    DataBufferUtils.release(requestBodyBuffer);

                    String method = exchange.getRequest().getMethod().name();
                    String route = exchange.getRequest().getPath().value();
                    UUID userId = getUserId(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
                    String requestHash = calculateRequestHash(method, userId, new String(requestBodyBytes, StandardCharsets.UTF_8),
                            queryToString(exchange), route);

                    IdempotencyPayload payload = new IdempotencyPayload()
                            .setUserId(userId)
                            .setIdempotencyKey(finalIdempotencyKey)
                            .setMethod(method)
                            .setRoute(route)
                            .setRequestHash(requestHash)
                            .setOperationId(UUID.randomUUID());

                    try {
                        return processRequest(exchange, chain, payload, requestBodyBytes);
                    } catch (Exception exception) {
                        return writeData(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                                new ErrorResponse(500, "Idempotency processing failed"));
                    }
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private Mono<Void> processRequest(ServerWebExchange exchange,
                                      GatewayFilterChain chain,
                                      IdempotencyPayload payload,
                                      byte[] requestBodyBytes) {
        Optional<IdempotencyRequest> existingRecord = idempotencyService.findByKey(payload);
        if (existingRecord.isPresent()) {
            return handleExistingKey(exchange, existingRecord.get(), payload.getRequestHash());
        }

        IdempotencyRequest record = idempotencyService.createRequestInProgress(payload);
        return forward(exchange, chain, record, requestBodyBytes);
    }

    private Mono<Void> handleExistingKey(ServerWebExchange exchange, IdempotencyRequest record, String requestHash) {
        if (!record.getRequestHash().equals(requestHash)) {
            return writeData(exchange, HttpStatus.CONFLICT, new ErrorResponse(409, "Idempotency-Key conflict"));
        }

        if (record.getStatus() == IdempotencyStatus.IN_PROGRESS) {
            return writeData(exchange, HttpStatus.ACCEPTED, new UuidResponseModel(record.getOperationId()));
        }

        return writeJsonToResponse(exchange, getRequestStatus(record), record.getResponseBody());
    }

    private Mono<Void> forward(ServerWebExchange exchange,
                               GatewayFilterChain chain,
                               IdempotencyRequest record,
                               byte[] requestBodyBytes) {
        CapturingResponse response = new CapturingResponse(exchange.getResponse());

        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> headers.set(IDEMPOTENCY_HEADER, record.getIdempotencyKey()))
                .build();

        ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(request) {
            @Override
            @NonNull
            public Flux<DataBuffer> getBody() {
                return Flux.defer(() -> Mono.just(exchange.getResponse().bufferFactory().wrap(requestBodyBytes)));
            }
        };

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(decoratedRequest)
                .response(response)
                .build();

        return chain.filter(mutatedExchange)
                .then(Mono.fromRunnable(() -> saveResponse(record, response)).then())
                .onErrorResume(exception -> {
                    idempotencyService.updateResponse(record, new ResponseData()
                            .setResponseCode(500)
                            .setResponseBody(formBodyToString(new ErrorResponse(500, "Ошибка сервера")))
                            .setStatus(IdempotencyStatus.FAILED));

                    return writeData(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                            new ErrorResponse(500, "Ошибка сервера"));
                });
    }

    private void saveResponse(IdempotencyRequest record, CapturingResponse response) {
        int responseCode = Optional.ofNullable(response.getStatusCode())
                .map(HttpStatusCode::value)
                .orElse(200);

        idempotencyService.updateResponse(record, new ResponseData()
                .setResponseCode(responseCode)
                .setResponseBody(response.getBody())
                .setStatus(responseCode >= 200 && responseCode < 400
                        ? IdempotencyStatus.COMPLETED
                        : IdempotencyStatus.FAILED));
    }

    private HttpStatusCode getRequestStatus(IdempotencyRequest record) {
        return HttpStatusCode.valueOf(record.getResponseCode() == null ? 200 : record.getResponseCode());
    }

    private Mono<Void> writeData(ServerWebExchange exchange, HttpStatusCode status, Object value) {
        return writeJsonToResponse(exchange, status, formBodyToString(value));
    }

    private Mono<Void> writeJsonToResponse(ServerWebExchange exchange, HttpStatusCode status, String body) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] responseBytes = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String formBodyToString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            return "{\"code\":500,\"message\":\"Ошибка сервера\"}";
        }
    }

    private boolean isIdempotentMethod(HttpMethod method) {
        return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE;
    }

    private UUID getUserId(String authorizationHeader) {
        return JwtAuthUtility.parseAuthorizationHeader(authorizationHeader).userId();
    }

    private String queryToString(ServerWebExchange exchange) {
        return exchange.getRequest().getQueryParams().entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().stream().sorted().map(value -> entry.getKey() + "=" + value))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
    }

    private String calculateRequestHash(String method, UUID userId, String requestBody, String requestQuery, String route) {
        String data = String.join("|", method, userId.toString(), requestBody, requestQuery, route);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hash.length * 2);

            for (byte singleByte : hash) {
                builder.append(String.format("%02x", singleByte));
            }

            return builder.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("Ошибка при подсчёте хеша запроса", exception);
        }
    }
}
