package ru.patterns.currency.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.currency.application.common.model.CurrencyResponseModel;
import ru.patterns.shared.model.client.ProcessedCurrencyModel;
import ru.patterns.currency.domain.entity.Currency;

@UtilityClass
public class CurrencyMapper {

    public ProcessedCurrencyModel toModel(Currency currency) {
        return new ProcessedCurrencyModel()
                .setId(currency.getId())
                .setName(currency.getName())
                .setCharCode(currency.getCharCode())
                .setSymbol(currency.getSymbol());
    }

    public CurrencyResponseModel toResponseModel(Currency currency) {
        return new CurrencyResponseModel()
                .setId(currency.getId())
                .setName(currency.getName())
                .setCharCode(currency.getCharCode())
                .setSymbol(currency.getSymbol())
                .setRate(currency.getRate());
    }
}
