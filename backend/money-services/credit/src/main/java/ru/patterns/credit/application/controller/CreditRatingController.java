package ru.patterns.credit.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.credit.application.common.model.response.CreditRatingModel;
import ru.patterns.credit.application.service.CreditRatingService;
import ru.patterns.shared.utility.AuthUtility;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/credit-account")
public class CreditRatingController {

    private final CreditRatingService creditRatingService;

    @GetMapping("/rating/{userId}")
    @Operation(description = "Получение кредитного рейтинга пользователя")
    public CreditRatingModel getUserCreditRating(@PathVariable UUID userId,
                                                 @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return creditRatingService.getUserCreditRatingModel(userId, authorization);
    }
}
