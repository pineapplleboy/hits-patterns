package ru.patterns.shared.model.kafka;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class TransferAssignmentMessage {

    private UUID requestId;

    private UUID operationId;

    private String accountNumberFrom;

    private UUID userIdFrom;

    private String accountNumberTo;

    private UUID userIdTo;

    private TransferAccountType transferAccountType;

    private BigDecimal amountFrom;

    private Integer currencyFrom;

    private BigDecimal amountTo;

    private Integer currencyTo;

    private int repeatAmount = 0;

    private OperationStatus status = OperationStatus.IN_PROCESS;
}
