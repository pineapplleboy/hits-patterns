package ru.patterns.monitoring.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.monitoring.application.common.LogResponseModel;
import ru.patterns.monitoring.domain.repository.LogRepository;
import ru.patterns.shared.exception.BadRequestException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public List<LogResponseModel> getLogs(Instant startTime, Instant endTime) {
        if (startTime.isAfter(endTime)) {
            throw new BadRequestException("endTime должен быть после startTime");
        }

        return logRepository.findAllByLogTimeBetweenOrderByLogTimeDesc(startTime, endTime)
                .stream()
                .map(log -> new LogResponseModel()
                        .setLogId(log.getLogId())
                        .setServiceId(log.getServiceId())
                        .setStatus(log.getStatus())
                        .setMessage(log.getMessage())
                        .setPath(log.getPath())
                        .setRequestBody(log.getRequestBody())
                        .setResponseBody(log.getResponseBody())
                        .setRequestUserId(log.getRequestUserId())
                        .setAuthorization(log.getAuthorization())
                        .setTraceId(log.getTraceId())
                        .setSpanId(log.getSpanId())
                        .setLogTime(log.getLogTime()))
                .toList();
    }
}
