package ru.patterns.account.application.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class BankAccountModel {

    private UUID id;

    private String accountNumber;

    private BigDecimal balance;
}
