package ru.patterns.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.patterns.account.application.common.enums.BankAccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BankAccount {
    @Id
    private UUID id = UUID.randomUUID();

    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankAccountType accountType; // мастер-счёт или счёт клиента

    @Column(nullable = false, scale = 2)
    private BigDecimal balance;

    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createTime = Instant.now();

    private Instant updateTime = Instant.now();

    @OneToMany(mappedBy = "bankAccountFrom", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("operationTime DESC")
    private List<BankAccountOperationHistory> outgoingOperations = new ArrayList<>();

    @OneToMany(mappedBy = "bankAccountTo")
    @OrderBy("operationTime DESC")
    private List<BankAccountOperationHistory> incomingOperations = new ArrayList<>();
}
