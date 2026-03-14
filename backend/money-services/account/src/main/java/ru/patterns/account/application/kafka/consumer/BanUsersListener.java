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
import ru.patterns.account.application.service.BanService;
import ru.patterns.shared.model.kafka.BanUserMessage;
import ru.patterns.shared.utility.AuthUtility;

@Slf4j
@Component
@RequiredArgsConstructor
public class BanUsersListener {

    private final BanService banService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.consumer.ban-users}")
    private String topic;

    @KafkaListener(topics = "${kafka.consumer.ban-users}", groupId = "${kafka.group}")
    public void listen(@Payload String message, @Header("Authorization") String token, Acknowledgment ack) {
        try {
            log.info("Получено сообщение из топика {}: {}", topic, message);

            BanUserMessage msg = objectMapper.readValue(message, BanUserMessage.class);

            AuthUtility.isAuthorized(token);

            banService.banUserAccounts(msg.getId(), msg.isBan());

            ack.acknowledge();
        } catch (Exception exception) {
            log.error("Ошибка при обработке сообщения, {}", exception.getMessage());

            ack.acknowledge();
        }
    }
}
