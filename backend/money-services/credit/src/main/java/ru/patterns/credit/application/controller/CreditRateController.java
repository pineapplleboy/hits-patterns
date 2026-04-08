package ru.patterns.credit.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.common.model.request.CreditRateDataModel;
import ru.patterns.credit.application.common.model.response.CreditRateModel;
import ru.patterns.credit.application.service.CreditRateCRUDService;
import ru.patterns.shared.model.response.UuidResponseModel;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/credit-rate")
public class CreditRateController {

    private final CreditRateCRUDService creditRateCRUDService;

    @Value("${service.name}")
    private String serviceName;

    @GetMapping("/available-plans")
    @Operation(summary = "Получение доступных кредитных тарифов [Все]")
    public List<CreditRateModel> getAvailablePlans(@RequestHeader(value = "traceId") String traceId) {
        var logData = TraceLogUtility.createDataForLogs(traceId, "", serviceName);

        return creditRateCRUDService.getCreditRates(logData);
    }

    @GetMapping("/available-plans/{id}")
    @Operation(summary = "Получение детальной информации о кредитном тарифе [Все]")
    public CreditRateModel getAvailablePlan(@PathVariable UUID id,
                                            @Parameter(hidden = true) @RequestHeader String authorization,
                                            @RequestHeader(value = "traceId") String traceId) {
        JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return creditRateCRUDService.getCreditRateById(id, logData);
    }

    @PostMapping()
    @Operation(summary = "Создание кредитного тарифа [Сотрудник]")
    public UuidResponseModel createCreditRate(@RequestBody CreditRateDataModel creditRateDataModel,
                                              @Parameter(hidden = true) @RequestHeader String authorization,
                                              @RequestHeader(value = "traceId") String traceId) {
        JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return creditRateCRUDService.createCreditRate(creditRateDataModel, logData);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление кредитного тарифа [Сотрудник]")
    public void updateCreateRate(@PathVariable UUID id, @RequestBody CreditRateDataModel creditRateDataModel,
                                 @Parameter(hidden = true) @RequestHeader String authorization,
                                 @RequestHeader(value = "traceId") String traceId) {
        JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        creditRateCRUDService.updateCreditRateModel(id, creditRateDataModel, logData);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление кредитного тарифа [Сотрудник]")
    public void deleteCreateRate(@PathVariable UUID id,
                                 @Parameter(hidden = true) @RequestHeader String authorization,
                                 @RequestHeader(value = "traceId") String traceId) {
        JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        creditRateCRUDService.deactivateCreditRateById(id, logData);
    }
}
