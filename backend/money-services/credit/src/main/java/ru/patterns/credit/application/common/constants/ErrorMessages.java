package ru.patterns.credit.application.common.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public static String CREDIT_RATE_NOT_FOUND = "Кредитный тариф не найден: ";
    public static String CREDIT_RATE_WITH_THAT_NAME_ALREADY_EXISTS = "Кредитный тариф с данным наименованием уже существует";
    public static String NEGATIVE_WRITE_OFF_DATE = "Время списания не может быть отрицательным";
    public static String INVALID_WRITE_OFF_DATE = "Некорректный формат периода. Ожидается формат: 1d1h1m1s или отдельные сочетания";
}
