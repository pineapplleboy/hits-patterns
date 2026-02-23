package ru.patterns.credit.application.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.kafka.TakeCreditMessage;

@Component
@RequiredArgsConstructor
public class CreditProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.take-credit-topic}")
    private String topic;

    public void send(TakeCreditMessage message) {
        kafkaTemplate.send(topic, String.valueOf(message.getApplicationId()), message);
    }
}
