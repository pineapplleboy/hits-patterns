package ru.patterns.transfers.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.kafka.TransferRequestMessage;
import ru.patterns.transfers.application.service.TransferRequestService;

@Component
@RequiredArgsConstructor
public class TransfersRequestListener {

    private final TransferRequestService transferRequestService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.consumer.transfer-request}", groupId = "${kafka.group}")
    public void listen(String message, Acknowledgment ack) {
        try {
            TransferRequestMessage msg = objectMapper.readValue(message, TransferRequestMessage.class);

            transferRequestService.processTransferRequest(msg);

            ack.acknowledge();
        } catch (Exception ignored) {
            ack.acknowledge();
        }
    }
}
