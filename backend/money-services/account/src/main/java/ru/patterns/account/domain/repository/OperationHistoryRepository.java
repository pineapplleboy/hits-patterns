package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.account.domain.entity.Operation;

import java.util.UUID;

public interface OperationHistoryRepository extends JpaRepository<Operation, UUID> {
}
