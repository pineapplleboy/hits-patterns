package ru.patterns.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain=true)
public class CreditAccount {
    @Id
    private UUID id = UUID.randomUUID();

    private UUID userId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false, scale = 2)
    private BigDecimal dept = BigDecimal.valueOf(0);

    private String creditRateName;

    private int creditRatePercent;

    private Duration writeOffPeriod;

    private Instant nextWriteOffDate;

    private boolean active;

    private boolean closed;

    @Column(nullable = false, updatable = false)
    private Instant createTime = Instant.now();

    private Instant updateTime = Instant.now();
}
