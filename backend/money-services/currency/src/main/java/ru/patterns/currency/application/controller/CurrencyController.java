package ru.patterns.currency.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.currency.application.common.model.CurrencyResponseModel;
import ru.patterns.currency.application.common.request.CalculatorRequestModel;
import ru.patterns.currency.application.service.CurrencyService;
import ru.patterns.shared.model.client.CurrencyAmountModel;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Value("${service.name}")
    private String serviceName;

    @GetMapping("/all-rates")
    @Operation(summary = "Получение всех доступных валют с тарифами [Все]")
    public List<CurrencyResponseModel> getAllCurrencies(@Parameter(hidden = true) @RequestHeader String authorization,
                                                        @RequestHeader(value = "traceId") String traceId,
                                                        HttpServletRequest request) {
        AuthUtility.isAuthorized(authorization);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return currencyService.getCurrencies(logData);
    }

    @GetMapping("/all-rates/{currencyId}")
    @Operation(summary = "Получение информации о конкретной валюте [Все]")
    public CurrencyResponseModel getCurrency(@PathVariable("currencyId") Integer currencyId,
                                             @Parameter(hidden = true) @RequestHeader String authorization,
                                             @RequestHeader(value = "traceId") String traceId,
                                             HttpServletRequest request) {
        AuthUtility.isAuthorized(authorization);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return currencyService.getCurrencyInfo(currencyId, logData);
    }

    @PostMapping("/calculate")
    @Operation(summary = "Конвертация валюты [Все]")
    public CurrencyAmountModel calculateTransferBetweenCurrencies(@RequestBody CalculatorRequestModel calculatorRequest,
                                                                  @Parameter(hidden = true) @RequestHeader String authorization,
                                                                  @RequestHeader(value = "traceId") String traceId,
                                                                  HttpServletRequest request) {
        AuthUtility.isAuthorized(authorization);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return currencyService.calculateAmount(
                calculatorRequest.getCurrencyIdFrom(),
                calculatorRequest.getCurrencyIdTo(),
                calculatorRequest.getAmount(),
                logData
        );
    }
}
