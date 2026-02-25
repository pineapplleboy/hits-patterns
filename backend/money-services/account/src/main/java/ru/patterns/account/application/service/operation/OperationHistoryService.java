package ru.patterns.account.application.service.operation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.model.enums.OperationStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationHistoryService {

    private final OperationRepository operationRepository;

    public void createAndSaveOperation(UUID userId,
                                       TransferAccountType transferAccountType,
                                       BigDecimal sum,
                                       AccountActionType actionType,
                                       OperationStatus operationStatus) {
        Operation operation = new Operation()
                .setAccountNumberFrom(null)
                .setUserIdFrom(userId)
                .setRecipientAccountNumber(null)
                .setRecipientId(null)
                .setAmount(sum)
                .setTransferAccountType(transferAccountType)
                .setActionType(actionType)
                .setStatus(operationStatus);

        operationRepository.save(operation);
    }
}
