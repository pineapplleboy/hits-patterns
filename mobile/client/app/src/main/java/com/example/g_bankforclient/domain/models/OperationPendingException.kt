package com.example.g_bankforclient.domain.models

/** Операция принята сервером, но ещё не завершена (статус IN_PROCESS/CREATED). */
class OperationPendingException(message: String) : Exception(message)
