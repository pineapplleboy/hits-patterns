package ru.patterns.notification.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.notification.application.common.TokenRequest;
import ru.patterns.notification.domain.entity.PushDevice;
import ru.patterns.notification.domain.repository.PushDeviceRepository;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PushDeviceService {

    private final PushSubscriptionService pushSubscriptionService;
    private final MonitoringLogger monitoringLogger;
    private final PushDeviceRepository pushDeviceRepository;

    @Transactional
    public void registerToken(TokenRequest tokenModel, AuthUser authUser, TracingLog logData) {
        PushDevice device = pushDeviceRepository.findByFcmToken(tokenModel.getToken())
                .orElseGet(PushDevice::new)
                .setActive(true)
                .setFcmToken(tokenModel.getToken())
                .setUserId(authUser.userId())
                .setUserRole(authUser.role())
                .setUpdatedAt(Instant.now());

        pushDeviceRepository.save(device);

        if (authUser.role() == Role.CLIENT) {
            pushSubscriptionService.subscribeClient(tokenModel.getToken(), authUser.userId(), logData);
        } else if (authUser.role() == Role.EMPLOYEE) {
            pushSubscriptionService.subscribeEmployee(tokenModel.getToken(), logData);
        }
    }

    @Transactional
    public void unsubscribeToken(TokenRequest tokenModel, AuthUser authUser, TracingLog logData) {
        pushDeviceRepository.findByFcmToken(tokenModel.getToken())
                .ifPresent(device -> {
                    device.setActive(false);
                    device.setUpdatedAt(Instant.now());
                    pushDeviceRepository.save(device);
                });

        if (authUser.role() == Role.CLIENT) {
            pushSubscriptionService.unsubscribeClient(tokenModel.getToken(), authUser.userId(), logData);
        } else if (authUser.role() == Role.EMPLOYEE) {
            pushSubscriptionService.unsubscribeEmployee(tokenModel.getToken(), logData);
        }
    }
}
