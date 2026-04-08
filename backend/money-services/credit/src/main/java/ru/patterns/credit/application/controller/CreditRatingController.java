package ru.patterns.credit.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.common.model.response.CreditRatingModel;
import ru.patterns.credit.application.service.CreditRatingService;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/credit-account")
public class CreditRatingController {

    private final CreditRatingService creditRatingService;

    @Value("${service.name}")
    private String serviceName;

    @GetMapping("/rating/{userId}")
    @Operation(description = "Получение кредитного рейтинга пользователя")
    public CreditRatingModel getUserCreditRating(@PathVariable UUID userId,
                                                 @Parameter(hidden = true) @RequestHeader String authorization,
                                                 @RequestHeader(value = "traceId") String traceId,
                                                 HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return creditRatingService.getUserCreditRating(userId, authorization, logData);
    }
}
