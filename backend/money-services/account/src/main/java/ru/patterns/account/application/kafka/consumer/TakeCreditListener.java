package ru.patterns.account.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.service.account.CreditAccountService;
import ru.patterns.shared.model.kafka.TakeCreditMessage;

@Component
@RequiredArgsConstructor
public class TakeCreditListener {

    private final CreditAccountService creditAccountService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.consumer.take-credit}", groupId = "${kafka.group}")
    public void listen(String message, Acknowledgment ack) {
        try {
            TakeCreditMessage msg = objectMapper.readValue(message, TakeCreditMessage.class);

            creditAccountService.takeCredit(msg);

            ack.acknowledge();
        } catch (Exception ignored) {

        }
    }
}
