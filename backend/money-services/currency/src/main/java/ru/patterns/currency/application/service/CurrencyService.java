package ru.patterns.currency.application.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.patterns.currency.application.client.RestClientConfig;
import ru.patterns.currency.application.common.model.AmountModel;
import ru.patterns.currency.application.common.model.CurrencyResponseModel;
import ru.patterns.currency.application.common.model.ProcessedCurrencyModel;
import ru.patterns.currency.application.config.CurrencyConfig;
import ru.patterns.currency.domain.entity.Currency;
import ru.patterns.currency.domain.mapper.CurrencyMapper;
import ru.patterns.currency.domain.repository.CurrencyRepository;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.exception.NotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@ExtensionMethod(CurrencyMapper.class)
public class CurrencyService {

    private final CurrencyConfig config;
    private final RestClientConfig restConfig;
    private final CurrencyRepository currencyRepository;

    @PostConstruct
    @Transactional
    public void init() {
        for (ProcessedCurrencyModel currencyModel : config.getProcessed()) {
            if (currencyRepository.findById(currencyModel.getId()).isPresent()) {
                continue;
            }

            currencyRepository.save(createCurrency(currencyModel));
        }
    }

    @Scheduled(fixedRate = 5 * 60000) // каждые 5 минут
    public void updateCurrenciesRate() {

    }

    public List<CurrencyResponseModel> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();

        if (currencies.isEmpty() || currencies.stream()
                .anyMatch(currency -> currency.getRate().equals(BigDecimal.ZERO))) {
            throwServiceUnavailableException();
        }

        return currencies.stream()
                .map(currency -> currency.toResponseModel())
                .toList();
    }

    public AmountModel calculateAmount(Integer currencyIdFrom, Integer currencyIdTo, BigDecimal amount) {
        Currency currencyFrom = currencyRepository.findById(currencyIdFrom)
                .orElseThrow(() -> new NotFoundException("Currency with id " + currencyIdFrom + " not found"));
        Currency currencyTo = currencyRepository.findById(currencyIdTo)
                .orElseThrow(() -> new NotFoundException("Currency with id " + currencyIdTo + " not found"));

        if (currencyFrom.getRate().equals(BigDecimal.ZERO) || currencyTo.getRate().equals(BigDecimal.ZERO)) {
            throwServiceUnavailableException();
        }

        return new AmountModel()
                .setFromCurrency(currencyFrom.toModel())
                .setToCurrency(currencyTo.toModel())
                .setAmount(amount)
                .setAmountFinal(calculateFinalAmount(currencyFrom, currencyTo, amount));
    }

    private Currency createCurrency(ProcessedCurrencyModel currencyModel) {
        return new Currency()
                .setId(currencyModel.getId())
                .setName(currencyModel.getName())
                .setCharCode(currencyModel.getCharCode())
                .setSymbol(currencyModel.getSymbol())
                .setRate(BigDecimal.ZERO);
    }

    private BigDecimal calculateFinalAmount(Currency from, Currency to, BigDecimal amount) {
        return amount
                .multiply(from.getRate())
                .divide(to.getRate(), 10, RoundingMode.HALF_EVEN)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private void throwServiceUnavailableException() {
        throw new BadRequestException("Service currently unavailable");
    }
}
