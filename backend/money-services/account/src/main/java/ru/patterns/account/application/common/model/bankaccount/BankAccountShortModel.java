package ru.patterns.account.application.common.model.bankaccount;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain=true)
public class BankAccountShortModel {

    private UUID id;

    private String accountNumber;

    private String balance;

    private boolean banned = false;

    private boolean hidden = false;
}
