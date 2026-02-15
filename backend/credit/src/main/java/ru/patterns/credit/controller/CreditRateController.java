package ru.patterns.credit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.credit.domain.request.CreditRateCreateModel;
import ru.patterns.credit.domain.response.CreditRateModel;
import ru.patterns.credit.domain.response.UuidResponseModel;
import ru.patterns.credit.service.CreditRateService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/credit-rate")
public class CreditRateController {
    private final CreditRateService creditRateService;

    @GetMapping("/available-plans")
    public List<CreditRateModel> getAvailablePlans() {
        return creditRateService.getCreditRates();
    }

    @PostMapping("/create")
    public UuidResponseModel createCreditRate(@RequestBody CreditRateCreateModel creditRateCreateModel) {
        return creditRateService.createCreditRate(creditRateCreateModel);
    }
}
