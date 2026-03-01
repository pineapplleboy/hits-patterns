package ru.patterns.account.application.common.enums;

public enum AccountActionType {
    OPEN_ACCOUNT,
    CLOSE_ACCOUNT,
    TRANSFER, // для обозначения типа операции
    TRANSFER_RECEIVED, // для возвращения в модельке
    TRANSFER_SENT, // для возвращения в модельке
    ACCOUNT_BANNED,
    ACCOUNT_UNBANNED
}
