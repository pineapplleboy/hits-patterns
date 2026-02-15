package ru.patterns.credit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.credit.domain.model.request.CreditRateDataModel;
import ru.patterns.credit.domain.model.response.CreditRateModel;
import ru.patterns.credit.domain.model.response.UuidResponseModel;
import ru.patterns.credit.service.CreditRateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/credit-rate")
public class CreditRateController {
    private final CreditRateService creditRateService;

    @GetMapping("/available-plans")
    public List<CreditRateModel> getAvailablePlans() {
        return creditRateService.getCreditRates();
    }

    @GetMapping("/available-plans/{id}")
    public CreditRateModel getAvailablePlan(@PathVariable UUID id) {
        return creditRateService.getCreditRateById(id);
    }

    @PostMapping()
    public UuidResponseModel createCreditRate(@RequestBody CreditRateDataModel creditRateDataModel) {
        return creditRateService.createCreditRate(creditRateDataModel);
    }

    @PutMapping("/{id}")
    public void updateCreateRate(@PathVariable UUID id, @RequestBody CreditRateDataModel creditRateDataModel) {
        creditRateService.updateCreditRateModel(id, creditRateDataModel);
    }

    @DeleteMapping("/{id}")
    public void deleteCreateRate(@PathVariable UUID id) {
        creditRateService.deactivateCreditRateById(id);
    }
}
