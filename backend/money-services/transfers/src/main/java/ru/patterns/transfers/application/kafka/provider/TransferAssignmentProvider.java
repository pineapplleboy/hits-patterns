package ru.patterns.transfers.application.kafka.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TransferAssignmentProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.provider.transfer-assignment}")
    private String topic;

    public void send(TransferAssignmentMessage message, String token, String traceId) {
        try {
            String payload = objectMapper.writeValueAsString(message);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, String.valueOf(message.getRequestId()), payload);

            record.headers().add(
                    new RecordHeader("Authorization", token.getBytes(StandardCharsets.UTF_8))
            );

            if (traceId != null) {
                record.headers().add(
                        new RecordHeader("traceId", traceId.getBytes(StandardCharsets.UTF_8))
                );
            }

            kafkaTemplate
                    .send(record)
                    .get(10, TimeUnit.SECONDS);

        } catch (Exception ignored) {
        }
    }
}
