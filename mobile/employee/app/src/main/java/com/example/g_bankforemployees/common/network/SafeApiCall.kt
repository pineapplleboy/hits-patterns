package com.example.g_bankforemployees.common.network

import org.json.JSONObject
import retrofit2.Response

suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> Response<T>,
    converter: (T) -> R
): Result<R> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(converter(body))
            } else {
                Result.failure(NullPointerException("Empty response"))
            }
        } else {
            Result.failure(IllegalStateException(extractErrorMessage(response)))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun safeApiCallUnit(
    apiCall: suspend () -> Response<Unit>
): Result<Unit> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException(extractErrorMessage(response)))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun extractErrorMessage(response: Response<*>): String {
    val errorBody = runCatching { response.errorBody()?.string().orEmpty() }
        .getOrDefault("")
        .trim()

    if (errorBody.isBlank()) {
        return response.message().takeUnless { it.isBlank() } ?: "Unknown error"
    }

    val messageFromJson = runCatching {
        JSONObject(errorBody).optString("message")
    }.getOrNull()

    return messageFromJson
        ?.takeUnless { it.isBlank() }
        ?: errorBody
}
