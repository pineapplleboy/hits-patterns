package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.account.domain.entity.CreditOperationHistory;

import java.util.UUID;

public interface CreditOperationHistoryRepository extends JpaRepository<CreditOperationHistory, UUID> {
}
