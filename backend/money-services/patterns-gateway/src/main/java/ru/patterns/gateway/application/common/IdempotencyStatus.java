package ru.patterns.gateway.application.common;

public enum IdempotencyStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}
