package ru.patterns.currency.application.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.currency.application.common.model.AmountModel;
import ru.patterns.currency.application.common.model.ProcessedCurrencyModel;
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
    public List<ProcessedCurrencyModel> getAllCurrencies(@Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.isAuthorized(authorization);

        return currencyService.getCurrencies();
    }

    @GetMapping("/calculate")
    public AmountModel calculateTransferBetweenCurrencies(@RequestBody CalculatorRequestModel calculatorRequest,
                                                          @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.isAuthorized(authorization);

        return currencyService.calculateAmount(calculatorRequest.getCurrencyIdFrom(), calculatorRequest.getCurrencyIdTo(), calculatorRequest.getAmount());
    }
}

