package ru.patterns.credit.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.common.model.response.CreditRatingModel;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditRatingService {

    // Рейтинг формируется автоматически с учетом платежной дисциплины, долговой нагрузки,
    // количества заявок на кредиты и срока кредитной истории

    public CreditRatingModel getUserCreditRatingModel(UUID userId, String token) {
        return new CreditRatingModel();
    }
}
