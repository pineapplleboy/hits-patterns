package ru.patterns.account.application.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.service.BanService;
import ru.patterns.shared.model.kafka.BanUserMessage;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

@Component
@RequiredArgsConstructor
public class BanUsersListener {

    private final BanService banService;
    private final ObjectMapper objectMapper;
    private final MonitoringLogger monitoringLogger;

    @Value("${kafka.consumer.ban-users}")
    private String topic;

    @Value("${service.name}")
    private String serviceName;

    @KafkaListener(topics = "${kafka.consumer.ban-users}", groupId = "${kafka.group}")
    public void listen(@Payload String message,
                       @Header("Authorization") String token,
                       @Header(value = "traceId", required = false) String traceId,
                       Acknowledgment ack) {
        try {
            var logData = TraceLogUtility.createDataForLogs(traceId, token, serviceName, topic,
                    JwtAuthUtility.parseAuthorizationHeader(token).userId());
            monitoringLogger.logInfo(logData, "Получено сообщение из топика " + topic, message, "-");

            BanUserMessage msg = objectMapper.readValue(message, BanUserMessage.class);

            AuthUtility.isAuthorized(token);
            banService.banUserAccounts(msg.getId(), msg.isBan());

            ack.acknowledge();
        } catch (Exception exception) {
            var logData = TraceLogUtility.createDataForLogs(traceId, token, serviceName, topic, null);
            monitoringLogger.logError(logData, "Ошибка при обработке сообщения: " + exception.getMessage(), message, "-");

            ack.acknowledge();
        }
    }
}
