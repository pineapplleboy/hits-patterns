package ru.patterns.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.patterns.account.application.common.enums.BankAccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BankAccount {
    @Id
    private UUID id = UUID.randomUUID();

    private UUID userId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankAccountType accountType; // мастер-счёт или счёт клиента

    @Column(nullable = false, scale = 2)
    private BigDecimal balance;

    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createTime = Instant.now();

    private Instant updateTime = Instant.now();
}
