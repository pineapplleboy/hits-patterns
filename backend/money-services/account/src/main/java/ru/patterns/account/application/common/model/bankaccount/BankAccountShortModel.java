package ru.patterns.account.application.common.model.bankaccount;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class BankAccountShortModel {

    private UUID id;

    private String accountNumber;

    private BigDecimal balance;

    private boolean banned = false;
}
