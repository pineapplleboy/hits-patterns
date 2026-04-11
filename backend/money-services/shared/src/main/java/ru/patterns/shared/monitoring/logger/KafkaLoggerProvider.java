package ru.patterns.shared.monitoring.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.monitoring.LogModel;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLoggerProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.provider.log-topic:00_logs}")
    private String topic;

    public void send(LogModel message) {
        try {
            String payload = objectMapper.writeValueAsString(message);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, String.valueOf(UUID.randomUUID()), payload);

            if (message.getTraceId() != null && !message.getTraceId().isBlank()) {
                record.headers().add(
                        new RecordHeader("traceId", message.getTraceId().getBytes(StandardCharsets.UTF_8))
                );
            }

            kafkaTemplate
                    .send(record)
                    .get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Ошибка отправления сообщения в Кафку: {}", e.getMessage());
        }
    }
}
