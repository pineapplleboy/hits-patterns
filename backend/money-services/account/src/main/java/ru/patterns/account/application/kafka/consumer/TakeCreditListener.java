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
import ru.patterns.account.application.service.account.CreditAccountService;
import ru.patterns.shared.model.kafka.TakeCreditMessage;
import ru.patterns.shared.utility.AuthUtility;

@Slf4j
@Component
@RequiredArgsConstructor
public class TakeCreditListener {

    private final CreditAccountService creditAccountService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.consumer.take-credit}")
    private String topic;

    @KafkaListener(topics = "${kafka.consumer.take-credit}", groupId = "${kafka.group}")
    public void listen(@Payload String message, @Header("Authorization") String token, Acknowledgment ack) {
        try {
            log.info("Получено сообщение из топика {}: {}", topic, message);

            TakeCreditMessage msg = objectMapper.readValue(message, TakeCreditMessage.class);

            AuthUtility.isAuthorized(token);

            creditAccountService.takeCredit(msg, token);

            ack.acknowledge();
        } catch (Exception exception) {
            log.error("Ошибка при обработке сообщения, {}", exception.getMessage());

            ack.acknowledge();
        }
    }
}
