package ru.patterns.credit.domain.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public static String CREDIT_RATE_NOT_FOUND = "Кредитный тариф не найден: ";
    public static String CREDIT_RATE_WITH_THAT_NAME_ALREADY_EXISTS = "Кредитный тариф с данным наименованием уже существует";
}
