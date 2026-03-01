package com.example.g_bankforclient.data.network.model

import java.util.Date
import java.util.UUID

data class AccountNumberResponseModel(
    val accountNumber: String
)

data class MoneyAmountRequestModel(
    val amount: Double
)

data class BankAccountShortModel(
    val id: UUID,
    val accountNumber: String,
    val balance: Double,
    val banned: Boolean = false
)

data class BankAccountFullModel(
    val id: UUID,
    val accountNumber: String,
    val balance: Double,
    val operations: List<OperationModel>,
    val createTime: Date,
    val banned: Boolean = false
)

data class CreditAccountShortModel(
    val id: UUID,
    val accountNumber: String,
    val dept: Double,
    val creditRateName: String,
    val creditRatePercent: Int,
    val writeOffPeriod: String,
    val nextWriteOffDate: Date
)

data class CreditAccountFullModel(
    val id: UUID,
    val accountNumber: String,
    val dept: Double,
    val creditRateName: String,
    val creditRatePercent: Int,
    val writeOffPeriod: String,
    val nextWriteOffDate: Date,
    val operations: List<OperationModel>
)

data class OperationModel(
    val operationId: UUID,
    val accountNumberFrom: String?,
    val userIdFrom: UUID?,
    val recipientAccountNumber: String?,
    val amount: Double,
    val transferAccountType: TransferAccountType,
    val actionType: AccountActionType,
    val status: OperationStatus,
    val createTime: Date
)

enum class TransferAccountType {
    BANK_ACCOUNT, CREDIT_ACCOUNT
}

enum class AccountActionType {
    OPEN_ACCOUNT, CLOSE_ACCOUNT, TRANSFER_RECEIVED, TRANSFER_SENT, ACCOUNT_BANNED, ACCOUNT_UNBANNED
}

enum class OperationStatus {
    CREATED, IN_PROCESS, SUCCESS, REJECTED
}

data class OperationStatusResponseModel(
    val status: OperationStatus
)
