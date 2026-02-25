package ru.patterns.account.application.common.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyAmountRequestModel {

    private BigDecimal amount;
}
