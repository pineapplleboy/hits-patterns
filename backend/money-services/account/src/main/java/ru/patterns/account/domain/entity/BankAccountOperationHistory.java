package ru.patterns.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.patterns.account.application.common.enums.AccountActionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BankAccountOperationHistory {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountActionType actionType;

    @Column(nullable = false, scale = 2)
    private BigDecimal writeOffAmount;

    @Column(nullable = false, scale = 2)
    private BigDecimal deptAfterOperation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_account_from_id", nullable = false)
    private BankAccount bankAccountFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_to_id")
    private BankAccount bankAccountTo;

    @Column(nullable = false, updatable = false)
    private Instant operationTime = Instant.now();

    private Instant updateTime = Instant.now();
}
