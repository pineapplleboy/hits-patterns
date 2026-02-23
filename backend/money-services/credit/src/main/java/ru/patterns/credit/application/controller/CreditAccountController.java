package ru.patterns.credit.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.credit.application.service.CreditAccountService;
import ru.patterns.shared.exception.UnauthorizedException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.JwtAuthUtility;

import java.math.BigDecimal;
import java.util.UUID;

import static ru.patterns.shared.model.constants.ErrorMessages.UNAUTHORIZED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/credit-account")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    @PostMapping("/take/{userId}/{rateId}")
    public OperationStatusResponseModel takeCredit(@PathVariable UUID userId, @PathVariable UUID rateId,
                                                   @RequestParam BigDecimal sum, @RequestHeader String authorization) {

        AuthUser authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);

        if (!authUser.userId().equals(userId)) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        return creditAccountService.takeCredit(userId, rateId, sum);
    }
}
