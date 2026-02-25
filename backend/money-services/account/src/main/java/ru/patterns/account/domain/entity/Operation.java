package ru.patterns.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.enums.OperationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "operation")
@Getter
@Setter
@Accessors(chain = true)
public class Operation {

    @Id
    @Column(name = "operation_id")
    private UUID operationId = UUID.randomUUID();

    @Column(name = "account_number")
    private String accountNumberFrom;

    @Column(name = "user_id_from")
    private UUID userIdFrom;

    @Column(name = "recipient_account_number")
    private String recipientAccountNumber;

    @Column(name = "recipient_id")
    private UUID recipientId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_to_account_type")
    private TransferAccountType transferAccountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private AccountActionType actionType = AccountActionType.OPEN_ACCOUNT;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status")
    private OperationStatus status = OperationStatus.CREATED;

    @Column(name = "operation_create_time")
    private Instant createTime = Instant.now();

    @Column(name = "update_time")
    private Instant updateTime = Instant.now();
}
