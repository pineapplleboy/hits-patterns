package ru.patterns.credit.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.service.CreditAccountService;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/credit-account")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    @Value("${service.name}")
    private String serviceName;

    @PostMapping("/take/{userId}/{rateId}")
    @Operation(summary = "Взятие кредита [Пользователь]")
    public OperationStatusResponseModel takeCredit(@PathVariable UUID userId, @PathVariable UUID rateId,
                                                   @RequestParam BigDecimal sum, @RequestParam String bankAccountNum,
                                                   @Parameter(hidden = true) @RequestHeader String authorization,
                                                   @RequestHeader(value = "traceId") String traceId) {

        AuthUtility.checkUserIdEquality(authorization, userId);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return creditAccountService.takeCredit(userId, rateId, sum, bankAccountNum, authorization, logData);
    }
}
