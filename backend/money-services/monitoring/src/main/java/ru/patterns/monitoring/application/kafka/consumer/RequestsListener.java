package ru.patterns.monitoring.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.patterns.monitoring.application.service.MonitoringDataService;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;
import ru.patterns.shared.utility.TraceLogUtility;

@Component
@RequiredArgsConstructor
public class RequestsListener {

    private final MonitoringDataService monitoringDataService;
    private final ObjectMapper objectMapper;
    private final MonitoringLogger monitoringLogger;

    @Value("${kafka.consumer.request-topic}")
    private String topic;

    @Value("${service.name}")
    private String serviceName;

    @KafkaListener(topics = "${kafka.consumer.request-topic}", groupId = "${kafka.group}")
    public void listen(@Payload String message,
                       @Header(value = "traceId", required = false) String traceId,
                       Acknowledgment ack) {
        try {
            var logData = TraceLogUtility.createDataForLogs(traceId, "", serviceName, topic, null);
            monitoringLogger.logInfo(logData, "Получено сообщение из топика " + topic, message, "-");

            RequestMonitoringModel msg = objectMapper.readValue(message, RequestMonitoringModel.class);

            monitoringDataService.addRequest(msg);

            ack.acknowledge();
        } catch (Exception exception) {
            var logData = TraceLogUtility.createDataForLogs(traceId, "", serviceName, topic, null);
            monitoringLogger.logError(logData, "Ошибка при обработке сообщения: " + exception.getMessage(), message, "-");

            ack.acknowledge();
        }
    }
}
