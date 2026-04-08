package ru.patterns.shared.monitoring.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRequestProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.provider.request-topic}")
    private String topic;

    public void send(RequestMonitoringModel message) {
        try {
            String payload = objectMapper.writeValueAsString(message);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, String.valueOf(UUID.randomUUID()), payload);

            kafkaTemplate
                    .send(record)
                    .get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Ошибка отправления сообщения в Кафку: {}", e.getMessage());
        }
    }
}
