package ru.patterns.shared.model.kafka;

import lombok.Data;
import ru.patterns.shared.model.enums.TransferAccountType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequestMessage {

    private UUID userIdFrom;

    private String accountNumberFrom;

    private UUID userIdTo;

    private String accountNumberTo;

    private String recipientName;

    private BigDecimal amount;

    private TransferAccountType transferType;

}
