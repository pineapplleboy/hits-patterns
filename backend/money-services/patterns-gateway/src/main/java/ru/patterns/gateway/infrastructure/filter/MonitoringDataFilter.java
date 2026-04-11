package ru.patterns.gateway.infrastructure.filter;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;
import ru.patterns.shared.model.monitoring.RequestResult;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;
import ru.patterns.shared.monitoring.request.RequestMonitoringService;
import ru.patterns.shared.utility.JwtAuthUtility;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class MonitoringDataFilter implements GlobalFilter, Ordered {

    private static final int MAX_LOG_BODY_LENGTH = 4096;

    private final MonitoringLogger monitoringLogger;
    private final RequestMonitoringService requestMonitoringService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTimeNanos = System.nanoTime();

        String endpoint = exchange.getRequest().getPath().value();

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String serviceFromHeader = exchange.getRequest().getHeaders().getFirst("serviceFrom");
        String traceIdFromHeader = exchange.getRequest().getHeaders().getFirst("traceId");
        String serviceId = resolveServiceId(exchange, serviceFromHeader);
        boolean skipBodyLogging = shouldSkipBodyLogging(endpoint);

        AtomicReference<String> requestBodyReference = new AtomicReference<>("");
        AtomicReference<StringBuilder> responseBodyReference = new AtomicReference<>(new StringBuilder());
        AtomicInteger responseStatusReference = new AtomicInteger(-1);

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .defaultIfEmpty(bufferFactory.wrap(new byte[0]))
                .flatMap(requestBodyBuffer -> {

                    byte[] requestBodyBytes = new byte[requestBodyBuffer.readableByteCount()];
                    requestBodyBuffer.read(requestBodyBytes);
                    DataBufferUtils.release(requestBodyBuffer);
                    if (!skipBodyLogging) {
                        requestBodyReference.set(limitBody(new String(requestBodyBytes, StandardCharsets.UTF_8)));
                    }

                    var decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        @NonNull
                        public Flux<DataBuffer> getBody() {
                            return Flux.defer(() -> Mono.just(bufferFactory.wrap(requestBodyBytes)));
                        }
                    };

                    var decoratedResponse = new ServerHttpResponseDecorator(exchange.getResponse()) {
                        @Override
                        @NonNull
                        public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                            return super.writeWith(Flux.from(body)
                                    .map(dataBuffer -> copyDataBuffer(dataBuffer, responseBodyReference, bufferFactory, skipBodyLogging))
                                    .doFinally(signalType -> captureStatus(getStatusCode(), responseStatusReference)));
                        }

                        @Override
                        @NonNull
                        public Mono<Void> writeAndFlushWith(@NonNull Publisher<? extends Publisher<? extends DataBuffer>> body) {
                            return writeWith(Flux.from(body).flatMapSequential(publisher -> publisher));
                        }

                        @Override
                        @NonNull
                        public Mono<Void> setComplete() {
                            captureStatus(getStatusCode(), responseStatusReference);
                            return super.setComplete();
                        }
                    };

                    return chain.filter(exchange.mutate()
                                    .request(decoratedRequest)
                                    .response(decoratedResponse)
                                    .build())
                            .doFinally(signalType -> {

                                long responseTimeMillis = (System.nanoTime() - startTimeNanos) / 1_000_000;

                                String requestBody = requestBodyReference.get();
                                String responseBody = responseBodyReference.get().toString();

                                int responseStatus = responseStatusReference.get();
                                RequestResult requestResult = getRequestResult(responseStatus);

                                UUID requestingUserId = extractUserId(authorizationHeader).orElse(null);

                                monitoringLogger.logInfo(formLogModel(traceIdFromHeader, UUID.randomUUID().toString(),
                                                authorizationHeader, requestingUserId, serviceId, endpoint),
                                        "Запрос", requestBody, responseBody);
                                requestMonitoringService.sendRequestToMonitoring(formRequestMonitoringService(endpoint, serviceId,
                                        requestResult, Duration.ofMillis(responseTimeMillis)));
                            });
                });
    }

    private static RequestResult getRequestResult(int responseStatus) {
        RequestResult requestResult;

        if (responseStatus >= 200 && responseStatus < 400) {
            requestResult = RequestResult.OK;
        } else if (responseStatus >= 400 && responseStatus < 500) {
            requestResult = RequestResult.USER_ERROR;
        } else {
            requestResult = RequestResult.SERVER_ERROR;
        }
        return requestResult;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 3;
    }

    private static DataBuffer copyDataBuffer(
            DataBuffer dataBuffer,
            AtomicReference<StringBuilder> responseBodyReference,
            DataBufferFactory bufferFactory,
            boolean skipBodyLogging
    ) {
        byte[] responseBodyBytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(responseBodyBytes);
        DataBufferUtils.release(dataBuffer);
        if (!skipBodyLogging && responseBodyReference.get().length() < MAX_LOG_BODY_LENGTH) {
            responseBodyReference.get().append(limitBody(new String(responseBodyBytes, StandardCharsets.UTF_8)));
        }
        return bufferFactory.wrap(responseBodyBytes);
    }

    private static void captureStatus(HttpStatusCode statusCode, AtomicInteger responseStatusReference) {
        responseStatusReference.set(statusCode != null ? statusCode.value() : -1);
    }

    private Optional<UUID> extractUserId(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return Optional.empty();
        }

        try {
            return Optional.of(JwtAuthUtility.parseAuthorizationHeader(authorizationHeader).userId());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private String resolveServiceId(ServerWebExchange exchange, String serviceFromHeader) {
        if (StringUtils.hasText(serviceFromHeader)) {
            return serviceFromHeader;
        }

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        return route != null ? route.getId() : null;
    }

    private static boolean shouldSkipBodyLogging(String endpoint) {
        if (!StringUtils.hasText(endpoint)) {
            return false;
        }

        String normalizedEndpoint = endpoint.toLowerCase(Locale.ROOT);
        return normalizedEndpoint.contains("/swagger")
                || normalizedEndpoint.contains("/swagger-ui")
                || normalizedEndpoint.contains("/v3/api-docs")
                || normalizedEndpoint.contains("/actuator")
                || normalizedEndpoint.contains("/favicon")
                || normalizedEndpoint.contains(".css")
                || normalizedEndpoint.contains(".js")
                || normalizedEndpoint.contains(".map")
                || normalizedEndpoint.contains(".html")
                || normalizedEndpoint.contains(".png")
                || normalizedEndpoint.contains(".svg")
                || normalizedEndpoint.contains("/monitoring/patterns/api/v2/logs");
    }

    private static String limitBody(String body) {
        if (!StringUtils.hasText(body) || body.length() <= MAX_LOG_BODY_LENGTH) {
            return body;
        }

        return body.substring(0, MAX_LOG_BODY_LENGTH) + "...[truncated]";
    }
    
    private TracingLog formLogModel(String traceId, String spanId, String authorization, UUID requestUserId, String serviceId, String path) {
        return new TracingLog()
                .setTraceId(traceId)
                .setSpanId(spanId)
                .setAuthorization(authorization)
                .setRequestUserId(requestUserId)
                .setServiceId(serviceId)
                .setPath(path);
    }
    
    private RequestMonitoringModel formRequestMonitoringService(String path, String serviceId, 
                                                                RequestResult requestResult, Duration responseTime) {
        return new RequestMonitoringModel()
                .setPath(path)
                .setServiceId(serviceId)
                .setRequestResult(requestResult)
                .setResponseTime(responseTime);
    }
}
