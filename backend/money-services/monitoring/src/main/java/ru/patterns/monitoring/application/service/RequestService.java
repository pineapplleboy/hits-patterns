package ru.patterns.monitoring.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.monitoring.application.common.RequestResponseModel;
import ru.patterns.monitoring.application.common.ServiceAverageResponseTimeModel;
import ru.patterns.monitoring.domain.entity.Request;
import ru.patterns.monitoring.domain.repository.RequestRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public List<RequestResponseModel> getRequests(Instant startTime, Instant endTime) {
        return requestRepository.findAllByRequestTimeBetweenOrderByRequestTimeDesc(startTime, endTime)
                .stream()
                .map(request -> new RequestResponseModel()
                        .setRequestId(request.getRequestId())
                        .setPath(request.getPath())
                        .setServiceId(request.getServiceId())
                        .setRequestResult(request.getRequestResult())
                        .setResponseTime(request.getResponseTime())
                        .setRequestTime(request.getRequestTime()))
                .toList();
    }

    public List<ServiceAverageResponseTimeModel> getAverageResponseTimeByServices(Instant startTime, Instant endTime) {
        return requestRepository.findAllByRequestTimeBetweenOrderByRequestTimeDesc(startTime, endTime)
                .stream()
                .collect(Collectors.groupingBy(request -> request.getServiceId() == null ? "Неизвестный сервис" : request.getServiceId()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ServiceAverageResponseTimeModel()
                        .setServiceId(entry.getKey())
                        .setAverageResponseTime(calculateAverageDuration(entry.getValue()
                                .stream()
                                .map(Request::getResponseTime)
                                .toList())))
                .toList();
    }

    private Duration calculateAverageDuration(List<Duration> durations) {
        var averageNanos = durations.stream()
                .mapToLong(Duration::toNanos)
                .average()
                .orElse(0);

        return Duration.ofNanos((long) averageNanos);
    }
}
