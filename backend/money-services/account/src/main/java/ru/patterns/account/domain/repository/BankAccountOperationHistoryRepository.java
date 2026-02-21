package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankAccountOperationHistoryRepository extends JpaRepository<BankAccountOperationHistoryRepository, UUID> {
}
