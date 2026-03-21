package ru.patterns.credit.application.common.model.operation;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.credit.application.common.enums.AccountActionType;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class OperationModel {

    private UUID operationId;

    private String accountNumberFrom;

    private UUID userIdFrom;

    private String recipientAccountNumber;

    private String amount;

    private TransferAccountType transferAccountType;

    private AccountActionType actionType = AccountActionType.OPEN_ACCOUNT;

    private OperationStatus status = OperationStatus.CREATED;

    private Instant createTime = Instant.now();

    private Instant operationResolveTime = Instant.now();
}
