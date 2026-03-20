package com.example.g_bankforclient.data.realtime

import com.example.g_bankforclient.domain.models.TransactionStatus
import com.example.g_bankforclient.domain.models.UserRealtimeEvent
import com.google.gson.JsonObject
import java.time.Instant
import java.util.Date

internal object CoreWsMessageParsers {

    fun parseEnvelope(payload: String, envelope: CoreWsEnvelope): UserRealtimeEvent? {
        return when (envelope.messageType) {
            "OPERATION_STATUS_UPDATE" -> parseOperationStatusUpdate(envelope.body)
            "OPERATION_CREATE" -> parseOperationCreate(envelope.body)
            "BANK_ACCOUNT_SUM_UPDATE" -> parseBankAccountSumUpdate(envelope.body)
            else -> null
        }
    }

    private fun parseOperationStatusUpdate(body: JsonObject): UserRealtimeEvent.OperationStatusUpdate? {
        val operationId = body["operationId"]?.asString ?: return null
        val accountNumberFrom = body["accountNumberFrom"]?.asString
        val recipientAccountNumber = body["recipientAccountNumber"]?.asString
        val amountRaw = body["amount"]?.asString
        val actionType = body["actionType"]?.asString
        val statusRaw = body["status"]?.asString ?: return null
        val status = parseTransactionStatus(statusRaw) ?: return null
        val createTimeRaw = body["createTime"]?.asString
        val createTime = parseIsoDate(createTimeRaw)

        val amount = parseAmount(amountRaw)
        return UserRealtimeEvent.OperationStatusUpdate(
            operationId = operationId,
            accountNumberFrom = accountNumberFrom,
            recipientAccountNumber = recipientAccountNumber,
            amount = amount,
            actionType = actionType,
            status = status,
            createTime = createTime
        )
    }

    private fun parseOperationCreate(body: JsonObject): UserRealtimeEvent.OperationCreate? {
        val operationId = body["operationId"]?.asString ?: return null
        val newStatusRaw = body["newStatus"]?.asString ?: return null
        val newStatus = parseTransactionStatus(newStatusRaw) ?: return null
        return UserRealtimeEvent.OperationCreate(
            operationId = operationId,
            newStatus = newStatus
        )
    }

    private fun parseBankAccountSumUpdate(body: JsonObject): UserRealtimeEvent.BankAccountSumUpdate? {
        val balanceRaw = body["balance"]?.asString ?: return null
        val balance = parseAmount(balanceRaw) ?: return null

        // Body формат может расширяться. Если сервер пришлет accountNumber — используем.
        val accountNumber = body["accountNumber"]?.asString
            ?: body["accountId"]?.asString
            ?: body["accountNumberFrom"]?.asString

        return UserRealtimeEvent.BankAccountSumUpdate(
            balance = balance,
            accountNumber = accountNumber
        )
    }

    private fun parseTransactionStatus(raw: String): TransactionStatus? = runCatching {
        TransactionStatus.valueOf(raw)
    }.getOrNull()

    private fun parseIsoDate(raw: String?): Date? {
        if (raw.isNullOrBlank()) return null
        return runCatching {
            Date.from(Instant.parse(raw))
        }.getOrNull()
    }

    private fun parseAmount(raw: String?): Double? {
        if (raw.isNullOrBlank()) return null
        // "86.15₽" / "50515.61₽" -> оставить только цифры, точку и минус.
        val normalized = raw.replace(',', '.')
        val cleaned = normalized.replace(Regex("[^0-9.\\-]"), "")
        if (cleaned.isBlank()) return null
        return cleaned.toDoubleOrNull()
    }
}

