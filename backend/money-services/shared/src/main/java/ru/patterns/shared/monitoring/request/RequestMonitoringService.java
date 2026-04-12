package ru.patterns.shared.monitoring.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;

@Service
@RequiredArgsConstructor
public class RequestMonitoringService {

    private final KafkaRequestProvider kafkaRequestProvider;

    public void sendRequestToMonitoring(RequestMonitoringModel message) {
        kafkaRequestProvider.send(message);
    }
}
