package ru.patterns.credit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.domain.response.CreditRateModel;
import ru.patterns.credit.mapper.CreditRateMapper;
import ru.patterns.credit.repository.CreditRateRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditRateService {
    private final CreditRateRepository creditRateRepository;

    public List<CreditRateModel> getCreditRates() {
        return creditRateRepository.findByIsActiveTrue().stream()
                .map(CreditRateMapper::toModel)
                .toList();
    }
}
