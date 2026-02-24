package ru.patterns.account.application.common.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.enums.OperationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class OperationModel {

    private UUID operationId;

    private String accountNumberFrom;

    private UUID userIdFrom;

    private String recipientAccountNumber;

    private BigDecimal amount;

    private TransferAccountType transferAccountType;

    private AccountActionType actionType = AccountActionType.OPEN_ACCOUNT;

    private OperationStatus status = OperationStatus.CREATED;

    private Instant createTime = Instant.now();
}
