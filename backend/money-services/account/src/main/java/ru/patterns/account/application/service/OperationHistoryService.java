package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.TransferAccountType;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.repository.OperationRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationHistoryService {

    private final OperationRepository operationRepository;

    public void createAndSaveOperationAccountCreation(UUID userId, TransferAccountType transferAccountType, BigDecimal sum) {
        Operation operation = new Operation()
                .setAccountNumberFrom(null)
                .setUserIdFrom(userId)
                .setRecipientAccountNumber(null)
                .setRecipientName(null)
                .setAmount(sum)
                .setTransferAccountType(transferAccountType);

        operationRepository.save(operation);
    }
}
