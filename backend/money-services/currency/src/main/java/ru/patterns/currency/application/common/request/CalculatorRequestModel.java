package ru.patterns.currency.application.common.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculatorRequestModel {

    private Integer currencyIdFrom;

    private Integer currencyIdTo;

    private BigDecimal amount;
}
