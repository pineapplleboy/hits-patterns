package ru.patterns.transfers.application.common.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CalculatorRequestModel {

    private Integer currencyIdFrom;

    private Integer currencyIdTo;

    private BigDecimal amount;
}
