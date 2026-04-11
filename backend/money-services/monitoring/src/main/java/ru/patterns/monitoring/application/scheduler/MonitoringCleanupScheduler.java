package ru.patterns.monitoring.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.monitoring.domain.repository.LogRepository;
import ru.patterns.monitoring.domain.repository.RequestRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringCleanupScheduler {

    private final LogRepository logRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredMonitoringData() {
        var thresholdData = Instant.now().minus(30, ChronoUnit.DAYS);

        logRepository.deleteByLogTimeBefore(thresholdData);
        requestRepository.deleteByRequestTimeBefore(thresholdData);
    }
}
