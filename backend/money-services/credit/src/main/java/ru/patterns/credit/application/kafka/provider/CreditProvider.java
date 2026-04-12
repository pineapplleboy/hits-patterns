package ru.patterns.credit.application.kafka.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.kafka.TakeCreditMessage;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CreditProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MonitoringLogger monitoringLogger;

    @Value("${app.kafka.take-credit-topic}")
    private String topic;

    @Value("${service.name}")
    private String serviceName;

    public void send(TakeCreditMessage message, String token) {
        send(message, token, null);
    }

    public void send(TakeCreditMessage message, String token, String traceId) {
        try {
            String payload = objectMapper.writeValueAsString(message);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, String.valueOf(message.getApplicationId()), payload);

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
        } catch (Exception exception) {
            var logData = TraceLogUtility.createDataForLogs(traceId, token, serviceName, topic, resolveUserId(token));
            monitoringLogger.logError(logData, "Ошибка отправления сообщения в Kafka: " + exception.getMessage(), String.valueOf(message), "-");
        }
    }

    private UUID resolveUserId(String token) {
        try {
            return JwtAuthUtility.parseAuthorizationHeader(token).userId();
        } catch (Exception exception) {
            return null;
        }
    }
}
