package ru.patterns.gateway.infrastructure.filter;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class MonitoringDataFilter implements GlobalFilter, Ordered {

    private final MonitoringLogger monitoringLogger;
    private final RequestMonitoringService requestMonitoringService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTimeNanos = System.nanoTime();

        String endpoint = exchange.getRequest().getPath().value();

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String serviceFromHeader = exchange.getRequest().getHeaders().getFirst("serviceFrom");
        String traceIdFromHeader = exchange.getRequest().getHeaders().getFirst("traceId");

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
                    requestBodyReference.set(new String(requestBodyBytes, StandardCharsets.UTF_8));

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
                                    .map(dataBuffer -> copyDataBuffer(dataBuffer, responseBodyReference, bufferFactory))
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

                                var requestingUserId = JwtAuthUtility.parseAuthorizationHeader(authorizationHeader).userId();

                                monitoringLogger.logInfo(formLogModel(traceIdFromHeader, UUID.randomUUID().toString(),
                                                authorizationHeader, requestingUserId, serviceFromHeader, endpoint),
                                        "Запрос", requestBody, responseBody);
                                requestMonitoringService.sendRequestToMonitoring(formRequestMonitoringService(endpoint, serviceFromHeader,
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
            DataBufferFactory bufferFactory
    ) {
        byte[] responseBodyBytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(responseBodyBytes);
        DataBufferUtils.release(dataBuffer);
        responseBodyReference.get().append(new String(responseBodyBytes, StandardCharsets.UTF_8));
        return bufferFactory.wrap(responseBodyBytes);
    }

    private static void captureStatus(HttpStatusCode statusCode, AtomicInteger responseStatusReference) {
        responseStatusReference.set(statusCode != null ? statusCode.value() : -1);
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
