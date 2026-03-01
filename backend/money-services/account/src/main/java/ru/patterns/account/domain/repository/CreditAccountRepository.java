package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.account.domain.entity.CreditAccount;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {
    List<CreditAccount> getCreditAccountByUserId(UUID userId);
    List<CreditAccount> getCreditAccountsByUserIdAndClosedIsFalse(UUID userId);
    Optional<CreditAccount> getByAccountNumberAndActiveAndUserId(String accountNumber, boolean active, UUID userId);
    List<CreditAccount> findAllByActiveIsTrueAndClosedIsFalseAndNextWriteOffDateLessThanEqual(Instant dateTime);
    Optional<CreditAccount> findCreditAccountByAccountNumberAndActiveTrueAndClosedFalse(String accountNumber);
}
