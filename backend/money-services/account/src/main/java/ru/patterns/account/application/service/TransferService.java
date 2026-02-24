package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    public OperationStatusResponseModel replenishMoney(UUID userId, MoneyAmountRequestModel requestModel) {
        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel withdrawMoney(UUID userId, MoneyAmountRequestModel requestModel) {
        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel payCredit(UUID userId, String creditAccountNumber, MoneyAmountRequestModel requestModel) {
        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }
}
