package ru.patterns.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.account.domain.entity.CreditAccount;

import java.util.UUID;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {
}
