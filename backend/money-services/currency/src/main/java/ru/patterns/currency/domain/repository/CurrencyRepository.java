package ru.patterns.currency.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.currency.domain.entity.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Optional<Currency> findByIdAndActiveTrue(Integer id);
    List<Currency> findAllByActiveTrue();
}
