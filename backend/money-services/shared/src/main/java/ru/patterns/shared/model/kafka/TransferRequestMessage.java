package ru.patterns.shared.model.kafka;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.shared.model.enums.TransferAccountType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class TransferRequestMessage {

    private UUID requestId = UUID.randomUUID();

    private UUID operationId;

    private String accountNumberFrom;

    private String accountNumberTo;

    private BigDecimal amount;

    private TransferAccountType transferType;
}
