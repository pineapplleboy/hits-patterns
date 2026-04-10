package com.example.g_bankforclient.data.network

class CircuitBreakerOpenException(
    serviceName: String,
    retryAfterMillis: Long,
) : IllegalStateException(
    "Сервис $serviceName временно недоступен. Повторите через ${retryAfterMillis / 1000 + 1} сек."
)
