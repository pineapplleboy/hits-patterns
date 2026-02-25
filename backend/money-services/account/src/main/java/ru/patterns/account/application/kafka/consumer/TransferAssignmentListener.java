package ru.patterns.account.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.service.transfer.TransferOperationService;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;

@Component
@RequiredArgsConstructor
public class TransferAssignmentListener {

    private final TransferOperationService transferOperationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.consumer.transfer-assignment}", groupId = "${kafka.group}")
    public void listen(String message, Acknowledgment ack) {
        try {
            TransferAssignmentMessage msg = objectMapper.readValue(message, TransferAssignmentMessage.class);

            transferOperationService.makeTransfer(msg);

            ack.acknowledge();
        } catch (Exception ignored) {
            ack.acknowledge();
        }
    }
}
