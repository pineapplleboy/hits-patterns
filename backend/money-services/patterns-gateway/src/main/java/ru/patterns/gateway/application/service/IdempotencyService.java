package ru.patterns.gateway.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.gateway.application.common.IdempotencyPayload;
import ru.patterns.gateway.domain.IdempotencyRequest;
import ru.patterns.gateway.application.common.IdempotencyStatus;
import ru.patterns.gateway.application.common.ResponseData;
import ru.patterns.gateway.infrastructure.repository.IdempotencyRequestRepository;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRequestRepository idempotencyRequestRepository;

    public Optional<IdempotencyRequest> findByKey(IdempotencyPayload payload) {
        return idempotencyRequestRepository.findByUserIdAndMethodAndRouteAndIdempotencyKey(
                payload.getUserId(),
                payload.getMethod(),
                payload.getRoute(),
                payload.getIdempotencyKey());
    }

    public IdempotencyRequest createRequestInProgress(IdempotencyPayload payload) {
        var newRequest = new IdempotencyRequest()
                .setUserId(payload.getUserId())
                .setIdempotencyKey(payload.getIdempotencyKey())
                .setMethod(payload.getMethod())
                .setRoute(payload.getRoute())
                .setRequestHash(payload.getRequestHash())
                .setOperationId(payload.getOperationId())
                .setStatus(IdempotencyStatus.IN_PROGRESS);

        return idempotencyRequestRepository.save(newRequest);
    }

    public void updateResponse(IdempotencyRequest request, ResponseData responseData) {
        var updatedRequest = request
                .setResponseCode(responseData.getResponseCode())
                .setResponseBody(responseData.getResponseBody())
                .setStatus(responseData.getStatus());

        idempotencyRequestRepository.save(updatedRequest);
    }

    public void deleteExpiredRequests(Instant thresholdTime) {
        idempotencyRequestRepository.deleteByRequestTimeBefore(thresholdTime);
    }
}
