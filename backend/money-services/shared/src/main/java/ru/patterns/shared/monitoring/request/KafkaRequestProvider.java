package ru.patterns.shared.monitoring.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class KafkaRequestProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MonitoringLogger monitoringLogger;

    @Value("${kafka.provider.request-topic:00_requests}")
    private String topic;

    public void send(RequestMonitoringModel message) {
        try {
            String payload = objectMapper.writeValueAsString(message);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, String.valueOf(UUID.randomUUID()), payload);

            kafkaTemplate
                    .send(record)
                    .get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            var logData = new TracingLog()
                    .setServiceId(message == null ? "" : message.getServiceId())
                    .setPath(topic);
            monitoringLogger.logError(logData, "Ошибка отправления сообщения в Kafka: " + exception.getMessage(),
                    String.valueOf(message), "-");
        }
    }
}
