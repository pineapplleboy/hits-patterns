package ru.patterns.account.application.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.service.BanService;
import ru.patterns.shared.model.kafka.BanUserMessage;

@Component
@RequiredArgsConstructor
public class KafkaBanUsersListener {

    private final BanService banService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.consumer.ban-users}", groupId = "${kafka.group}")
    public void listen(String message) {
        try {
            BanUserMessage msg = objectMapper.readValue(message, BanUserMessage.class);

            banService.banUserAccounts(msg.getId(), msg.isBan());
        } catch (Exception ignored) {

        }
    }
}
