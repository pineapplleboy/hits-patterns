package ru.patterns.account.application.common.model.operation;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class OperationUpdateStatusModel {

    private UUID operationId;

    private OperationStatus newStatus;
}
