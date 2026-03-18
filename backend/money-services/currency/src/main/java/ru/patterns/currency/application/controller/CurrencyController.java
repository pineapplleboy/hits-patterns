package ru.patterns.currency.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.shared.model.client.CurrencyAmountModel;
import ru.patterns.currency.application.common.model.CurrencyResponseModel;
import ru.patterns.currency.application.common.request.CalculatorRequestModel;
import ru.patterns.currency.application.service.CurrencyService;
import ru.patterns.shared.utility.AuthUtility;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/all-rates")
    @Operation(summary = "Получение всех доступных валют с тарифами [Все]")
    public List<CurrencyResponseModel> getAllCurrencies(@Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.isAuthorized(authorization);

        return currencyService.getCurrencies();
    }

    @GetMapping("/all-rates/{currencyId}")
    @Operation(summary = "Получение информации о конкретной валюте [Все]")
    public CurrencyResponseModel getCurrency(@PathVariable("currencyId") Integer currencyId,
                                             @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.isAuthorized(authorization);

        return currencyService.getCurrencyInfo(currencyId);
    }

    @PostMapping("/calculate")
    @Operation(summary = "Конвертация валюты [Все]")
    public CurrencyAmountModel calculateTransferBetweenCurrencies(@RequestBody CalculatorRequestModel calculatorRequest,
                                                                  @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.isAuthorized(authorization);

        return currencyService.calculateAmount(calculatorRequest.getCurrencyIdFrom(), calculatorRequest.getCurrencyIdTo(), calculatorRequest.getAmount());
    }
}

