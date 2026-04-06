package ru.patterns.monitoring.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.monitoring.domain.entity.Log;
import ru.patterns.monitoring.domain.entity.Request;
import ru.patterns.monitoring.domain.repository.LogRepository;
import ru.patterns.monitoring.domain.repository.RequestRepository;
import ru.patterns.shared.model.monitoring.LogModel;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;

@Service
@RequiredArgsConstructor
public class MonitoringDataService {

    private final LogRepository logRepository;
    private final RequestRepository requestRepository;

    public void addLog(LogModel log) {
        var newLog = new Log()
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
                .setLogTime(log.getLogTime());

        logRepository.save(newLog);
    }

    public void addRequest(RequestMonitoringModel request) {
        var newRequest = new Request()
                .setPath(request.getPath())
                .setServiceId(request.getServiceId())
                .setRequestResult(request.getRequestResult())
                .setResponseTime(request.getResponseTime())
                .setRequestTime(request.getRequestTime());

        requestRepository.save(newRequest);
    }
}
