package ru.patterns.account.application.kafka.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.kafka.TransferRequestMessage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TransferRequestProvider {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.provider.transfer-request}")
    private String topic;

    public void send(TransferRequestMessage message, String token) {
        try {
            String payload = objectMapper.writeValueAsString(message);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, String.valueOf(message.getRequestId()), payload);

            record.headers().add(
                    new RecordHeader("Authorization", token.getBytes(StandardCharsets.UTF_8))
            );

            kafkaTemplate
                    .send(record)
                    .get(10, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize TransferRequestMessage: " + e.getOriginalMessage());
        } catch (Exception e) {
            throw new BadRequestException("Failed to send TransferRequestMessage to Kafka: " + e.getMessage());
        }
    }
}
