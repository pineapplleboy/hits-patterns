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
                ?: parseOperationCreate(envelope.body)

            "OPERATION_CREATE" -> parseOperationStatusUpdate(envelope.body)
                ?: parseOperationCreate(envelope.body)
            "BANK_ACCOUNT_SUM_UPDATE" -> parseBankAccountSumUpdate(envelope.body)
            "CREDIT_ACCOUNT_DEPT_UPDATE" -> parseCreditAccountDeptUpdate(envelope.body)
            else -> null
        }
    }

    private fun JsonObject.str(key: String): String? =
        get(key)?.takeIf { !it.isJsonNull }?.asString

    private fun parseOperationStatusUpdate(body: JsonObject): UserRealtimeEvent.OperationStatusUpdate? {
        val operationId = body.str("operationId") ?: return null
        val accountNumberFrom = body.str("accountNumberFrom")
        val recipientAccountNumber = body.str("recipientAccountNumber")
        val amountRaw = body.str("amount")
        val actionType = body.str("actionType")
        val statusRaw = body.str("status") ?: return null
        val status = parseTransactionStatus(statusRaw) ?: return null
        val createTimeRaw = body.str("createTime")
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
        val operationId = body.str("operationId") ?: return null
        val newStatusRaw = body.str("newStatus") ?: return null
        val newStatus = parseTransactionStatus(newStatusRaw) ?: return null
        return UserRealtimeEvent.OperationCreate(
            operationId = operationId,
            newStatus = newStatus
        )
    }

    private fun parseBankAccountSumUpdate(body: JsonObject): UserRealtimeEvent.BankAccountSumUpdate? {
        val balanceRaw = body.str("balance") ?: return null
        val balance = parseAmount(balanceRaw) ?: return null

        val accountNumber = body.str("accountNumber")

        return UserRealtimeEvent.BankAccountSumUpdate(
            balance = balance,
            accountNumber = accountNumber
        )
    }

    private fun parseCreditAccountDeptUpdate(body: JsonObject): UserRealtimeEvent.CreditAccountDeptUpdate? {
        val balanceRaw = body.str("balance") ?: return null
        val balance = parseAmount(balanceRaw) ?: return null
        val accountNumber = body.str("accountNumber") ?: body.str("creditAccountNumber")
        return UserRealtimeEvent.CreditAccountDeptUpdate(
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
        val normalized = raw.replace(',', '.')
        val cleaned = normalized.replace(Regex("[^0-9.\\-]"), "")
        if (cleaned.isBlank()) return null
        return cleaned.toDoubleOrNull()
    }
}

