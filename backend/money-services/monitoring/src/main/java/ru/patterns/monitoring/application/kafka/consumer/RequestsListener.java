package ru.patterns.monitoring.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.patterns.monitoring.application.service.MonitoringDataService;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;
import ru.patterns.shared.utility.AuthUtility;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestsListener {

    private final MonitoringDataService monitoringDataService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.consumer.request-topic}")
    private String topic;

    @KafkaListener(topics = "${kafka.consumer.request-topic}", groupId = "${kafka.group}")
    public void listen(@Payload String message, @Header("Authorization") String token, Acknowledgment ack) {
        try {
            log.info("Получено сообщение из топика {}: {}", topic, message);

            RequestMonitoringModel msg = objectMapper.readValue(message, RequestMonitoringModel.class);

            AuthUtility.isAuthorized(token);

            monitoringDataService.addRequest(msg);

            ack.acknowledge();
        } catch (Exception exception) {
            log.error("Ошибка при обработке сообщения, {}", exception.getMessage());

            ack.acknowledge();
        }
    }
}
