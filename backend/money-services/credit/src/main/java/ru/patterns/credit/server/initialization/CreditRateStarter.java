package ru.patterns.credit.server.initialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.patterns.credit.domain.initialization.CreditRateInitDto;
import ru.patterns.credit.entity.CreditRate;
import ru.patterns.credit.repository.CreditRateRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class CreditRateStarter implements ApplicationRunner {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CreditRateRepository creditRateRepository;
    private static final Path PATH = Path.of("config/credit-rates-init.json");

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Запущена обработка инициализационных кредитных тарифов");

        if (!Files.exists(PATH)) {
            log.warn("Файл credit-rates-init.json не найден, обработка прекращена");
            return;
        }

        List<CreditRateInitDto> creditRates = mapper.readValue(
                PATH.toFile(),
                new TypeReference<>() {
                }
        );

        if (creditRates == null || creditRates.isEmpty()) return;

        for (var creditRate : creditRates) {
            Optional<CreditRate> existingCreditRate = creditRateRepository.findByNameAndIsActiveTrue(creditRate.getName());

            if (existingCreditRate.isPresent()) {
                var existingRate = existingCreditRate.get();

                existingRate.setPercent(creditRate.getPercent());
                existingRate.setWriteOffPeriod(creditRate.getWriteOffPeriod());
                existingRate.setUpdateTime(Instant.now());

                log.info("Тариф {} обновлён", existingRate.getName());

                creditRateRepository.save(existingRate);
            } else {
                var newRate = new CreditRate(creditRate.getName(), creditRate.getPercent(), creditRate.getWriteOffPeriod());

                log.info("Создан новый тариф: {}", newRate.getName());

                creditRateRepository.save(newRate);
            }
        }

        log.info("Обработка инициализационных кредитных тарифов завершена");
    }
}
