package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.account.domain.entity.BankAccountOperationHistory;

import java.util.UUID;

public interface BankAccountOperationHistoryRepository extends JpaRepository<BankAccountOperationHistory, UUID> {
}
