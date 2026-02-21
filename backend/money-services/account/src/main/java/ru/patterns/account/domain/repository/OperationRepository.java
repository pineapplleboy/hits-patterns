package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.account.application.common.enums.TransferAccountType;
import ru.patterns.account.domain.entity.Operation;

import java.util.List;
import java.util.UUID;

public interface OperationRepository extends JpaRepository<Operation, UUID> {
    List<Operation> findByUserIdFrom(UUID userId);
    List<Operation> findByAccountNumberFromAndTransferAccountType(String accountNumberFrom, TransferAccountType transferAccountType);
}
