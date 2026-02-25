package ru.patterns.transfers.application.kafka.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TransferAssignmentProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.provider.transfer-assignment}")
    private String topic;

    public void send(TransferAssignmentMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate
                    .send(topic, String.valueOf(message.getRequestId()), payload)
                    .get(10, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize TransferAssignmentMessage: " + e.getOriginalMessage());
        } catch (Exception e) {
            throw new BadRequestException("Failed to send TransferAssignmentMessage to Kafka: " + e.getMessage());
        }
    }
}
