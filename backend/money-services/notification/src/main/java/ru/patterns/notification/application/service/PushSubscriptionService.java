package ru.patterns.notification.application.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PushSubscriptionService {

    private static final String EMPLOYEES_TOPIC = "employees";
    private static final String CLIENT_TOPIC = "client";

    private final MonitoringLogger monitoringLogger;

    public void subscribeClient(String token, UUID userId, TracingLog logData) {
        subscribe(token, calculateClientTopic(userId), logData);
    }

    public void unsubscribeClient(String token, UUID userId, TracingLog logData) {
        unsubscribe(token, calculateClientTopic(userId), logData);
    }

    public void subscribeEmployee(String token, TracingLog logData) {
        subscribe(token, EMPLOYEES_TOPIC, logData);
    }

    public void unsubscribeEmployee(String token, TracingLog logData) {
        unsubscribe(token, EMPLOYEES_TOPIC, logData);
    }

    public String getEmployeesTopic() {
        return EMPLOYEES_TOPIC;
    }

    public String getClientTopic(UUID userId) {
        return calculateClientTopic(userId);
    }

    private void subscribe(String token, String topic, TracingLog logData) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(token), topic);
            monitoringLogger.logInfo(logData, "Tокен подписан на топик = " + topic + ", token = " + token);
        } catch (FirebaseMessagingException e) {
            throw new BadRequestException("Ошибка подписки на топик " + topic, logData);
        }
    }

    private void unsubscribe(String token, String topic, TracingLog logData) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(token), topic);
            monitoringLogger.logInfo(logData, "Tокен отписан от топика = " + topic + ", token = " + token);
        } catch (FirebaseMessagingException e) {
            throw new BadRequestException("Ошибка отписки от топика топик " + topic, logData);
        }
    }

    private String calculateClientTopic(UUID userId) {
        return CLIENT_TOPIC + "_" + userId.toString();
    }
}
