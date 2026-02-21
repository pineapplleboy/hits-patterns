package ru.patterns.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.enums.OperationStatus;
import ru.patterns.account.application.common.enums.TransferAccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Operation {

    @Id
    private UUID operationId;

    @Column(nullable = false)
    private String accountNumberFrom;

    @Column(nullable = false)
    private String recipientAccountNumber;

    @Column(nullable = true)
    private String recipientName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferAccountType transferAccountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountActionType actionType = AccountActionType.OPEN_ACCOUNT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationStatus status = OperationStatus.CREATED;

    private Instant createTime = Instant.now();

    private Instant updateTime = Instant.now();
}
