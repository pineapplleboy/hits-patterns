package ru.patterns.credit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.credit.entity.CreditRate;

import java.util.List;
import java.util.UUID;

public interface CreditRateRepository extends JpaRepository<CreditRate, UUID> {
    List<CreditRate> findByIsActiveTrue();
}
