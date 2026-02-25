package ru.patterns.account.application.kafka.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.kafka.TransferRequestMessage;

@Component
@RequiredArgsConstructor
public class TransferRequestProvider {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.provider.transfer-request}")
    private String topic;

    public void send(TransferRequestMessage message) {
        kafkaTemplate.send(topic, String.valueOf(message.getRequestId()), message);
    }
}
