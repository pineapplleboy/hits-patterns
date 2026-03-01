package ru.patterns.account.application.common.model.bankaccount;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.account.application.common.model.OperationModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class BankAccountFullModel {

    private UUID id;

    private String accountNumber;

    private BigDecimal balance;

    private List<OperationModel> operations;

    private Instant createTime;

    private boolean banned = false;
}
