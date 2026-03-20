package com.example.g_bankforclient.domain.models

sealed interface UserRealtimeEvent {

    data class OperationStatusUpdate(
        val operationId: String,
        val accountNumberFrom: String? = null,
        val recipientAccountNumber: String? = null,
        val amount: Double? = null,
        val actionType: String? = null,
        val status: TransactionStatus,
        val createTime: java.util.Date? = null,
    ) : UserRealtimeEvent

    data class OperationCreate(
        val operationId: String,
        val newStatus: TransactionStatus,
    ) : UserRealtimeEvent

    data class BankAccountSumUpdate(
        val balance: Double,
        val accountNumber: String? = null,
    ) : UserRealtimeEvent
}

