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
public class CreditOperationHistory {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credit_account_id", nullable = false)
    private CreditAccount creditAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountActionType actionType;

    @Column(nullable = false, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, scale = 2)
    private BigDecimal deptAfterOperation;

    @Column(nullable = false, updatable = false)
    private Instant operationTime = Instant.now();

    private Instant updateTime = Instant.now();
}
