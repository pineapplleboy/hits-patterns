package ru.patterns.notification.application.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.notification.domain.repository.PushDeviceRepository;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.notification.NotificationModel;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationSenderService {

    private static final String TITLE = "Новое уведомление";

    private final PushSubscriptionService pushSubscriptionService;
    private final MonitoringLogger monitoringLogger;
    private final PushDeviceRepository pushDeviceRepository;

    public void send(NotificationModel notificationModel, TracingLog logData) {
        sendToTopic(pushSubscriptionService.getEmployeesTopic(), notificationModel.getMessage(), logData);

        if (notificationModel.getUserId() != null && hasActiveUserDevices(notificationModel)) {
            sendToTopic(pushSubscriptionService.getClientTopic(notificationModel.getUserId()),
                    notificationModel.getMessage(), logData);
        }
    }

    private boolean hasActiveUserDevices(NotificationModel notificationModel) {
        return pushDeviceRepository.existsByUserIdAndActiveTrueAndNotificationsEnabledUntilAfter(
                notificationModel.getUserId(),
                Instant.now()
        );
    }

    private void sendToTopic(String topic, String message, TracingLog logData) {
        try {
            FirebaseMessaging.getInstance().send(Message.builder()
                    .setTopic(topic)
                    .putData("title", TITLE)
                    .putData("message", message)
                    .build());

            monitoringLogger.logInfo(logData, "Уведомление отправлено в топик " + topic, message, "-");
        } catch (Exception exception) {
            throw new BadRequestException("Ошибка отправки уведомления в топик " + topic, logData);
        }
    }
}
