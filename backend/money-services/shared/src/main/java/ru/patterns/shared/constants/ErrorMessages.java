package ru.patterns.shared.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public final String USER_NOT_FOUND = "Пользователь не найден";
    public final String FORBIDDEN = "Доступ запрещён";
    public final String ACCOUNT_NOT_FOUND = "Счёт не найден";
    public final String CREDIT_NOT_FOUND = "Кредит не найден";
    public final String UNAUTHORIZED = "Ошибка авторизации!";
    public final String INCORRECT_REQUEST_AMOUNT = "Некорректная сумма запроса";
    public final String ACCOUNT_BANNED = "Счёт заблокирован";
    public final String ONLY_WITH_RUB = "Данную операцию можно осуществлять только с RUB";
    public final String TRANSFERS_BETWEEN_CURRENCIES_NOT_AVAILABLE = "Перевод другому человеку в другой валюте запрещён";
    public final String SERVICE_CURRENTLY_UNAVAILABLE = "Сервис в данный момент недоступен";
    public final String CREDIT_RATE_NOT_FOUND = "Тарифный план кредита не найден";
    public final String CURRENCY_NOT_FOUND = "Валюта не найдена";
    public final String INVALID_CREDIT_SUM = "Некорректный лимит";
    public final String CURRENCY_NOT_SUPPORTABLE = "Валюта не поддерживается";
    public final String OPERATION_NOT_FOUND = "Операция не найдена";
}
