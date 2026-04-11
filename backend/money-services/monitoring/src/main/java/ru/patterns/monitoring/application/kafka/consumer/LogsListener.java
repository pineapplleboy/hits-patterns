package ru.patterns.monitoring.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.patterns.monitoring.application.service.MonitoringDataService;
import ru.patterns.shared.model.monitoring.LogModel;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogsListener {

    private final MonitoringDataService monitoringDataService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.consumer.log-topic}")
    private String topic;

    @KafkaListener(topics = "${kafka.consumer.log-topic}", groupId = "${kafka.group}")
    public void listen(@Payload String message, Acknowledgment ack) {
        try {
            log.info("Получено сообщение из топика {}.", topic);

            LogModel msg = objectMapper.readValue(message, LogModel.class);
            monitoringDataService.addLog(msg);

            ack.acknowledge();
        } catch (Exception exception) {
            log.error("Ошибка при обработке сообщения из топика {}: {}", topic, exception.getMessage());
            ack.acknowledge();
        }
    }
}
