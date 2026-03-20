package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.domain.entity.Operation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationRepository extends JpaRepository<Operation, UUID> {
    List<Operation> findByUserIdFrom(UUID userId);
    List<Operation> findByRecipientId(UUID recipientId);
    List<Operation> findByAccountNumberFromAndTransferAccountType(String accountNumberFrom, TransferAccountType transferAccountType);
    List<Operation> findByRecipientAccountNumberAndTransferAccountType(String recipientAccountNumber, TransferAccountType transferAccountType);
    List<Operation> findByUserIdFromAndTransferAccountTypeAndActionTypeAndPurchasedFalseAndExpectedPaymentDateBefore(
            UUID userIdFrom,
            TransferAccountType transferAccountType,
            AccountActionType actionType,
            Instant expectedPaymentDate
    );
    List<Operation> findByUserIdFromAndTransferAccountTypeAndActionTypeAndPurchasedFalseAndDeptLeftGreaterThan(
            UUID userIdFrom,
            TransferAccountType transferAccountType,
            AccountActionType actionType,
            BigDecimal deptLeft
    );
    Optional<Operation> getOperationByOperationId(UUID operationId);
}
