package ru.patterns.transfers.application.kafka.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;

@Component
@RequiredArgsConstructor
public class TransferAssignmentProvider {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.provider.transfer-assignment}")
    private String topic;

    public void send(TransferAssignmentMessage message) {
        kafkaTemplate.send(topic, String.valueOf(message.getRequestId()), message);
    }
}
