package ru.patterns.transfers.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.patterns.shared.model.kafka.TransferRequestMessage;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.transfers.application.service.TransferRequestService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransfersRequestListener {

    private final TransferRequestService transferRequestService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.consumer.transfer-request}")
    private String topic;

    @KafkaListener(topics = "${kafka.consumer.transfer-request}", groupId = "${kafka.group}")
    public void listen(@Payload String message,
                       @Header("Authorization") String token,
                       @Header(value = "traceId", required = false) String traceId,
                       Acknowledgment ack) {
        try {
            log.info("Получено сообщение из топика {}: {}", topic, message);

            TransferRequestMessage msg = objectMapper.readValue(message, TransferRequestMessage.class);

            AuthUtility.isAuthorized(token);

            try {
                transferRequestService.processTransferRequest(msg, token, traceId);
            } catch (Exception exception) {
                transferRequestService.processReject(msg, token, traceId);
            }

            ack.acknowledge();
        } catch (Exception exception) {
            log.error("Ошибка при обработке сообщения: {}", exception.getMessage());

            ack.acknowledge();
        }
    }
}
