package ru.patterns.account.application.kafka.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.kafka.TransferRequestMessage;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TransferRequestProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.provider.transfer-request}")
    private String topic;

    public void send(TransferRequestMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate
                    .send(topic, String.valueOf(message.getRequestId()), payload)
                    .get(10, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize TransferRequestMessage: " + e.getOriginalMessage());
        } catch (Exception e) {
            throw new BadRequestException("Failed to send TransferRequestMessage to Kafka: " + e.getMessage());
        }
    }
}
