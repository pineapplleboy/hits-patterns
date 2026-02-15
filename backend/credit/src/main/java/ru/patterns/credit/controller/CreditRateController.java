package ru.patterns.credit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.service.CreditRateService;

@RestController
@RequiredArgsConstructor
public class CreditRateController {
    private final CreditRateService creditRateService;
}
