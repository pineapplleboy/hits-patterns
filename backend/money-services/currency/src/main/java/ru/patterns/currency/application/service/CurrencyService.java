package ru.patterns.currency.application.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.currency.application.common.model.AmountModel;
import ru.patterns.currency.application.common.model.ProcessedCurrencyModel;
import ru.patterns.currency.application.config.CurrencyConfig;
import ru.patterns.currency.domain.mapper.CurrencyMapper;
import ru.patterns.currency.domain.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@ExtensionMethod(CurrencyMapper.class)
public class CurrencyService {

    private final CurrencyConfig config;
    private final CurrencyRepository currencyRepository;

    public void updateCurrenciesRate() {

    }

    public List<ProcessedCurrencyModel> getCurrencies() {
        return null;
    }

    public AmountModel calculateAmount(Integer currencyIdFrom, Integer currencyIdTo, BigDecimal amount) {
        return null;
    }
}
