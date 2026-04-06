package ru.patterns.monitoring.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.monitoring.application.common.RequestResponseModel;
import ru.patterns.monitoring.domain.repository.RequestRepository;

import java.time.Instant;
import java.util.List;

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
                        .setResponseTime(request.getResponseTime())
                        .setRequestTime(request.getRequestTime()))
                .toList();
    }
}
