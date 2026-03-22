package com.example.g_bankforclient.data.network.model

import java.util.Date
import java.util.UUID

data class AccountNumberResponseModel(
    val accountNumber: String
)

data class MoneyAmountRequestModel(
    val amount: Double
)

/** Модель валюты (в ответах core API). */
data class CurrencyModel(
    val id: Int,
    val name: String,
    val charCode: String,
    val symbol: String,
    val rate: Double
)

data class BankAccountShortModel(
    val id: UUID,
    val accountNumber: String,
    /** Баланс может приходить строкой с бэкенда (v2). */
    val balance: String,
    val banned: Boolean = false,
    val hidden: Boolean = false
)

data class BankAccountFullModel(
    val id: UUID,
    val accountNumber: String,
    // Backend sometimes returns money as a string with currency suffix (e.g. "86.15₽", "86.15Р").
    val balance: String,
    val operations: List<OperationModel>,
    val createTime: Date,
    val banned: Boolean = false,
    val hidden: Boolean = false,
    val currency: CurrencyModel? = null
)

data class CreditAccountShortModel(
    val id: UUID,
    val accountNumber: String,
    /** Долг приходит строкой с суффиксом валюты, например "50515.61₽" */
    val dept: String,
    val creditRateName: String,
    val creditRatePercent: Int,
    val writeOffPeriod: String,
    val nextWriteOffDate: Date,
    val banned: Boolean = false
)

data class CreditAccountFullModel(
    val id: UUID,
    val accountNumber: String,
    /** Долг приходит строкой с суффиксом валюты, например "50515.61₽" */
    val dept: String,
    val creditRateName: String,
    val creditRatePercent: Int,
    val writeOffPeriod: String,
    val nextWriteOffDate: Date,
    val operations: List<OperationModel>,
    val banned: Boolean = false
)

data class OperationModel(
    val operationId: UUID,
    val accountNumberFrom: String?,
    val userIdFrom: UUID?,
    val recipientAccountNumber: String?,
    // Backend sometimes returns money as a string with currency suffix.
    val amount: String,
    val transferAccountType: TransferAccountType,
    val actionType: AccountActionType,
    val status: OperationStatus,
    val createTime: Date
)

enum class TransferAccountType {
    BANK_ACCOUNT, CREDIT_ACCOUNT
}

enum class AccountActionType {
    OPEN_ACCOUNT, CLOSE_ACCOUNT, TRANSFER, TRANSFER_RECEIVED, TRANSFER_SENT,
    ACCOUNT_BANNED, ACCOUNT_UNBANNED, CREDIT_DEPT_PERCENT
}

enum class OperationStatus {
    CREATED, IN_PROCESS, SUCCESS, REJECTED
}

data class OperationStatusResponseModel(
    val status: OperationStatus
)
