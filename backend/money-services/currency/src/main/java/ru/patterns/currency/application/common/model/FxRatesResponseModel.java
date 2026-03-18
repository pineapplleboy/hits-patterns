package ru.patterns.currency.application.common.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class FxRatesResponseModel {

    private boolean success;

    private String terms;

    private String privacy;

    private Long timestamp;

    private String date;

    private String base;

    private Map<String, BigDecimal> rates;
}
