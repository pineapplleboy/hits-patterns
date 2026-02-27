package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.domain.entity.Operation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationRepository extends JpaRepository<Operation, UUID> {
    List<Operation> findByUserIdFrom(UUID userId);
    List<Operation> findByRecipientId(UUID recipientId);
    List<Operation> findByAccountNumberFromAndTransferAccountTypeAndUserIdFrom(String accountNumberFrom, TransferAccountType transferAccountType, UUID userIdFrom);
    List<Operation> findByRecipientAccountNumberAndTransferAccountTypeAndRecipientId(String recipientAccountNumber, TransferAccountType transferAccountType, UUID recipientId);
    Optional<Operation> getOperationByOperationId(UUID operationId);
}
