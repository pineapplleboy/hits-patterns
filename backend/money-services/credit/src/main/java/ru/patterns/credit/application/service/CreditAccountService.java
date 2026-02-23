package ru.patterns.credit.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditAccountService {

    public OperationStatusResponseModel takeCredit(UUID userId, UUID rateId) {
        return new OperationStatusResponseModel(OperationStatus.IN_PROCESS);
    }
}
