package ru.patterns.account.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.common.enums.TransactionFinishStatus;
import ru.patterns.account.application.kafka.provider.TransferRequestProvider;
import ru.patterns.account.application.service.transfer.TransferOperationService;
import ru.patterns.shared.factory.TransferMessageFactory;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;
import ru.patterns.shared.utility.AuthUtility;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferAssignmentListener {

    private final TransferOperationService transferOperationService;
    private final ObjectMapper objectMapper;
    private final TransferRequestProvider transferRequestProvider;

    @Value("${kafka.consumer.transfer-assignment}")
    private String topic;

    @KafkaListener(topics = "${kafka.consumer.transfer-assignment}", groupId = "${kafka.group}")
    public void listen(@Payload String message,
                       @Header("Authorization") String token,
                       @Header(value = "traceId", required = false) String traceId,
                       Acknowledgment ack) {
        try {
            log.info("Получено сообщение из топика {}: {}", topic, message);

            TransferAssignmentMessage msg = objectMapper.readValue(message, TransferAssignmentMessage.class);

            AuthUtility.isAuthorized(token);

            var transferResult = transferOperationService.makeTransfer(msg, traceId);

            if (transferResult == TransactionFinishStatus.TRANSACTION_PAUSED) {
                transferRequestProvider.send(TransferMessageFactory.createRepeatRequest(msg), token, traceId);
            }

            ack.acknowledge();
        } catch (Exception exception) {
            log.error("Ошибка при обработке сообщения: {}", exception.getMessage());

            ack.acknowledge();
        }
    }
}
