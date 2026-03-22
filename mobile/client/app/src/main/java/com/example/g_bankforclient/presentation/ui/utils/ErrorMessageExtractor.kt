package com.example.g_bankforclient.presentation.ui.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.HttpException

/**
 * Извлекает читаемое сообщение об ошибке из Throwable.
 *
 * Для HttpException читает тело ответа и пробует достать поля:
 *   "message" (основной формат бэка: {"code":404,"message":"Счёт не найден"})
 *   "detail"  (формат identity-сервера)
 *   "error"   (запасной вариант)
 *
 * Если ни одно не найдено — возвращает [fallback].
 */
fun Throwable.toUserMessage(fallback: String = "Произошла ошибка"): String {
    if (this is HttpException) {
        val parsed = runCatching {
            val body = response()?.errorBody()?.string()
            if (body.isNullOrBlank()) return@runCatching null
            val json = JsonParser.parseString(body).asJsonObject
            json.tryString("message")
                ?: json.tryString("detail")
                ?: json.tryString("error")
        }.getOrNull()
        if (!parsed.isNullOrBlank()) return parsed
        // Если тело не распарсилось — хотя бы показываем HTTP статус по-русски
        return httpStatusToRussian(code()) ?: "HTTP ${code()}"
    }
    return message?.takeIf { it.isNotBlank() } ?: fallback
}

private fun JsonObject.tryString(key: String): String? =
    runCatching { get(key)?.takeIf { !it.isJsonNull }?.asString }.getOrNull()

private fun httpStatusToRussian(code: Int): String? = when (code) {
    400 -> "Некорректный запрос"
    401 -> "Необходима авторизация"
    403 -> "Доступ запрещён"
    404 -> "Ресурс не найден"
    409 -> "Конфликт данных"
    422 -> "Ошибка валидации данных"
    429 -> "Слишком много запросов"
    500 -> "Внутренняя ошибка сервера"
    502 -> "Сервер временно недоступен"
    503 -> "Сервис недоступен"
    else -> null
}
