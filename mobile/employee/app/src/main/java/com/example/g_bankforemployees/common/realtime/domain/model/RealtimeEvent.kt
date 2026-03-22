package com.example.g_bankforemployees.common.realtime.domain.model

data class RealtimeOperation(
    val operationId: String,
    val accountNumberFrom: String? = null,
    val userIdFrom: String? = null,
    val recipientAccountNumber: String? = null,
    val amount: String,
    val transferAccountType: String,
    val actionType: String,
    val status: String,
    val createTime: String,
)

sealed interface RealtimeEvent {
    data class OperationUpsert(
        val operation: RealtimeOperation,
    ) : RealtimeEvent

    data class OperationStatusChanged(
        val operationId: String,
        val newStatus: String,
    ) : RealtimeEvent

    data class BankAccountBalanceChanged(
        val balance: String,
        val accountId: String?,
        val accountNumber: String?,
    ) : RealtimeEvent

    data class CreditAccountDebtChanged(
        val balance: String,
        val accountId: String?,
        val accountNumber: String?,
    ) : RealtimeEvent

    data class Unknown(
        val type: String,
    ) : RealtimeEvent
}
