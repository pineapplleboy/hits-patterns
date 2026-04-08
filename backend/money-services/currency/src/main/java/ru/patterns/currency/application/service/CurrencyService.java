package ru.patterns.currency.application.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.patterns.currency.application.client.RestClientConfig;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.model.client.CurrencyAmountModel;
import ru.patterns.currency.application.common.model.CurrencyResponseModel;
import ru.patterns.currency.application.common.model.FxRatesResponseModel;
import ru.patterns.shared.model.client.ProcessedCurrencyModel;
import ru.patterns.currency.application.config.CurrencyConfig;
import ru.patterns.currency.domain.entity.Currency;
import ru.patterns.currency.domain.mapper.CurrencyMapper;
import ru.patterns.currency.domain.repository.CurrencyRepository;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.log.TracingLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
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
        FxRatesResponseModel response = restConfig.settingsClient()
                .get()
                .retrieve()
                .body(FxRatesResponseModel.class);

        if (response == null || !response.isSuccess() || response.getRates() == null) {
            return;
        }

        for (ProcessedCurrencyModel currencyModel : config.getProcessed()) {

            var actualRate = response.getRates().get(currencyModel.getCharCode());

            if (actualRate == null) {
                log.error("Не получается обновить валюту {}", currencyModel.getCharCode());
                continue;
            }

            var currency = currencyRepository.findById(currencyModel.getId()).
                    orElseThrow(() -> new NotFoundException("Валюта с айди" + currencyModel.getId() + " не найдена"));

            currency.setRate(actualRate);
            currencyRepository.save(currency);
        }
    }

    public List<CurrencyResponseModel> getCurrencies(TracingLog dataForLog) {
        List<Currency> currencies = currencyRepository.findAllByActiveTrue();

        if (currencies.isEmpty() || currencies.stream()
                .anyMatch(currency -> currency.getRate().equals(BigDecimal.ZERO))) {
            throwServiceUnavailableException();
        }

        return currencies.stream()
                .map(currency -> currency.toResponseModel())
                .toList();
    }

    public CurrencyResponseModel getCurrencyInfo(Integer currencyId, TracingLog logData) {
        var currency = currencyRepository.findByIdAndActiveTrue(currencyId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CURRENCY_NOT_FOUND));

        if (currency.getRate().equals(BigDecimal.ZERO)) {
            throwServiceUnavailableException();
        }

        return currency.toResponseModel();
    }

    public CurrencyAmountModel calculateAmount(Integer currencyIdFrom, Integer currencyIdTo, BigDecimal amount, TracingLog logData) {
        Currency currencyFrom = currencyRepository.findByIdAndActiveTrue(currencyIdFrom)
                .orElseThrow(() -> new NotFoundException("Валюта с айди " + currencyIdFrom + " не найдена"));
        Currency currencyTo = currencyRepository.findByIdAndActiveTrue(currencyIdTo)
                .orElseThrow(() -> new NotFoundException("Валюта с айди " + currencyIdTo + " не найдена"));

        if (currencyFrom.getRate().equals(BigDecimal.ZERO) || currencyTo.getRate().equals(BigDecimal.ZERO)) {
            throwServiceUnavailableException();
        }

        return new CurrencyAmountModel()
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
                .multiply(to.getRate())
                .divide(from.getRate(), 10, RoundingMode.HALF_EVEN)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private void throwServiceUnavailableException() {
        throw new BadRequestException(ErrorMessages.SERVICE_CURRENTLY_UNAVAILABLE);
    }
}
