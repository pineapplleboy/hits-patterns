package ru.patterns.credit.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.credit.entity.CreditRate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditRateRepository extends JpaRepository<CreditRate, UUID> {
    List<CreditRate> findByIsActiveTrue();
    Optional<CreditRate> findByIdAndIsActiveTrue(UUID id);
    Optional<CreditRate> findByNameAndIsActiveTrue(@NotNull String name);
}
