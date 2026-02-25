package ru.patterns.shared.model.kafka;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.shared.model.enums.TransferAccountType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class TransferRequestMessage {

    private UUID requestId;

    private UUID operationId;

    private UUID userIdFrom;

    private String accountNumberFrom;

    private UUID userIdTo;

    private String accountNumberTo;

    private BigDecimal amount;

    private TransferAccountType transferType;

}
