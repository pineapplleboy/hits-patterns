package ru.patterns.monitoring.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.monitoring.application.common.RequestResponseModel;
import ru.patterns.monitoring.application.common.ServiceAverageResponseTimeModel;
import ru.patterns.monitoring.application.common.ServiceRequestResultPercentModel;
import ru.patterns.monitoring.application.common.ServiceRequestsPerSecondModel;
import ru.patterns.monitoring.domain.entity.Request;
import ru.patterns.monitoring.domain.repository.RequestRepository;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.monitoring.RequestResult;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public List<RequestResponseModel> getRequests(Instant startTime, Instant endTime, TracingLog logData) {
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

    public List<ServiceAverageResponseTimeModel> getAverageResponseTimeByServices(Instant startTime, Instant endTime, TracingLog logData) {
        return requestRepository.findAllByRequestTimeBetweenOrderByRequestTimeDesc(startTime, endTime)
                .stream()
                .collect(Collectors.groupingBy(request -> request.getServiceId() == null ? "РќРµРёР·РІРµСЃС‚РЅС‹Р№ СЃРµСЂРІРёСЃ" : request.getServiceId()))
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

    public List<ServiceRequestResultPercentModel> getRequestResultPercentsByServices(Instant startTime, Instant endTime, TracingLog logData) {
        return requestRepository.findAllByRequestTimeBetweenOrderByRequestTimeDesc(startTime, endTime)
                .stream()
                .collect(Collectors.groupingBy(request -> request.getServiceId() == null ? "РќРµРёР·РІРµСЃС‚РЅС‹Р№ СЃРµСЂРІРёСЃ" : request.getServiceId()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    var requests = entry.getValue();
                    var total = requests.size();

                    return new ServiceRequestResultPercentModel()
                            .setServiceId(entry.getKey())
                            .setOkPercent(calculatePercent(total, countByResult(requests, RequestResult.OK)))
                            .setUserErrorPercent(calculatePercent(total, countByResult(requests, RequestResult.USER_ERROR)))
                            .setServerErrorPercent(calculatePercent(total, countByResult(requests, RequestResult.SERVER_ERROR)));
                })
                .toList();
    }

    public List<ServiceRequestsPerSecondModel> getRequestsPerSecondByServices(Instant startTime, Instant endTime, TracingLog logData) {
        var intervalSeconds = Duration.between(startTime, endTime).toMillis() / 1000.0;

        return requestRepository.findAllByRequestTimeBetweenOrderByRequestTimeDesc(startTime, endTime)
                .stream()
                .collect(Collectors.groupingBy(request -> request.getServiceId() == null ? "РќРµРёР·РІРµСЃС‚РЅС‹Р№ СЃРµСЂРІРёСЃ" : request.getServiceId()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ServiceRequestsPerSecondModel()
                        .setServiceId(entry.getKey())
                        .setRequestsPerSecond(calculateRequestsPerSecond(entry.getValue().size(), intervalSeconds)))
                .toList();
    }

    private Duration calculateAverageDuration(List<Duration> durations) {
        var averageNanos = durations.stream()
                .mapToLong(Duration::toNanos)
                .average()
                .orElse(0);

        return Duration.ofNanos((long) averageNanos);
    }

    private long countByResult(List<Request> requests, RequestResult requestResult) {
        return requests.stream()
                .filter(request -> requestResult.equals(request.getRequestResult()))
                .count();
    }

    private double calculatePercent(int total, long count) {
        if (total == 0) {
            return 0;
        }

        return count * 100.0 / total;
    }

    private double calculateRequestsPerSecond(int count, double intervalSeconds) {
        if (intervalSeconds <= 0) {
            return 0;
        }

        return count / intervalSeconds;
    }
}
