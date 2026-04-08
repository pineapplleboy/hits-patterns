package ru.patterns.gateway.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.gateway.application.service.IdempotencyService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyCleanupScheduler {

    private final IdempotencyService idempotencyService;

    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void deleteExpiredIdempotencyRequests() {
        var thresholdTime = Instant.now().minus(20, ChronoUnit.MINUTES);
        idempotencyService.deleteExpiredRequests(thresholdTime);
    }
}
