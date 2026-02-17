package ru.patterns.credit.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.credit.application.common.model.request.CreditRateDataModel;
import ru.patterns.credit.application.common.model.response.CreditRateModel;
import ru.patterns.shared.model.response.UuidResponseModel;
import ru.patterns.credit.application.service.CreditRateCRUDService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/credit-rate")
public class CreditRateController {
    private final CreditRateCRUDService creditRateCRUDService;

    @GetMapping("/available-plans")
    public List<CreditRateModel> getAvailablePlans() {
        return creditRateCRUDService.getCreditRates();
    }

    @GetMapping("/available-plans/{id}")
    public CreditRateModel getAvailablePlan(@PathVariable UUID id) {
        return creditRateCRUDService.getCreditRateById(id);
    }

    @PostMapping()
    public UuidResponseModel createCreditRate(@RequestBody CreditRateDataModel creditRateDataModel) {
        return creditRateCRUDService.createCreditRate(creditRateDataModel);
    }

    @PutMapping("/{id}")
    public void updateCreateRate(@PathVariable UUID id, @RequestBody CreditRateDataModel creditRateDataModel) {
        creditRateCRUDService.updateCreditRateModel(id, creditRateDataModel);
    }

    @DeleteMapping("/{id}")
    public void deleteCreateRate(@PathVariable UUID id) {
        creditRateCRUDService.deactivateCreditRateById(id);
    }
}
