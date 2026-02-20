package ru.patterns.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CreditAccount {
    @Id
    private UUID id = UUID.randomUUID();

    private UUID userId;

    @Column(nullable = false, scale = 2)
    private BigDecimal dept = BigDecimal.valueOf(0);

    private String creditRateName;

    private int creditRatePercent;

    private Duration writeOffPeriod;

    private Instant nextWriteOffDate;

    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createTime = Instant.now();

    private Instant updateTime = Instant.now();

    @OneToMany(mappedBy = "creditAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("operationTime DESC")
    private List<CreditOperationHistory> operations = new ArrayList<>();
}
