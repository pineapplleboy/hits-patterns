package ru.patterns.currency.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.currency.domain.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}
