package ru.patterns.gateway.infrastructure.capture;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

public class CapturingResponse extends ServerHttpResponseDecorator {

    private final DataBufferFactory bufferFactory;
    private final AtomicReference<StringBuilder> body = new AtomicReference<>(new StringBuilder());

    public CapturingResponse(ServerHttpResponse delegate) {
        super(delegate);
        this.bufferFactory = delegate.bufferFactory();
    }

    @Override
    @NonNull
    public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
        return super.writeWith(Flux.from(body).map(this::captureAndCopy));
    }

    @Override
    @NonNull
    public Mono<Void> writeAndFlushWith(@NonNull Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMapSequential(publisher -> publisher));
    }

    public String getBody() {
        return body.get().toString();
    }

    private DataBuffer captureAndCopy(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];

        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        body.get().append(new String(bytes, StandardCharsets.UTF_8));

        return bufferFactory.wrap(bytes);
    }
}
