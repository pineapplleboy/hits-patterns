package ru.patterns.account.application.common.enums;

public enum AccountActionType {
    OPEN_ACCOUNT,
    CLOSE_ACCOUNT,
    TRANSFER, // для обозначения типа операции
    TRANSFER_RECEIVED, // для
    TRANSFER_SENT,
    ACCOUNT_BANNED,
    ACCOUNT_UNBANNED
}
