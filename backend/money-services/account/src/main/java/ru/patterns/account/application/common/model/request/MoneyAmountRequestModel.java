package ru.patterns.account.application.common.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MoneyAmountRequestModel {

    private BigDecimal amount;
}
