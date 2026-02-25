package ru.patterns.credit.application.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.kafka.TakeCreditMessage;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CreditProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.take-credit-topic}")
    private String topic;

    public void send(TakeCreditMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate
                    .send(topic, String.valueOf(message.getApplicationId()), payload)
                    .get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new BadRequestException("Failed to send TakeCreditMessage to Kafka: " + e.getMessage());
        }
    }
}
