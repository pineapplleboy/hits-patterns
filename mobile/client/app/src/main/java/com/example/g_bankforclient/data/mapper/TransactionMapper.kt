package com.example.g_bankforclient.data.mapper

import com.example.g_bankforclient.data.network.model.AccountActionType
import com.example.g_bankforclient.data.network.model.OperationModel
import com.example.g_bankforclient.data.network.model.OperationStatus
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.models.TransactionStatus
import com.example.g_bankforclient.domain.models.TransactionType

fun OperationModel.toDomain(fallbackAccountId: String = ""): Transaction = Transaction(
    id = operationId.toString(),
    accountId = accountNumberFrom ?: fallbackAccountId,
    type = when (actionType) {
        AccountActionType.OPEN_ACCOUNT -> TransactionType.DEPOSIT
        AccountActionType.CLOSE_ACCOUNT -> TransactionType.WITHDRAWAL
        AccountActionType.TRANSFER_RECEIVED -> TransactionType.DEPOSIT
        AccountActionType.TRANSFER_SENT -> TransactionType.WITHDRAWAL
        AccountActionType.ACCOUNT_BANNED -> TransactionType.INFO
        AccountActionType.ACCOUNT_UNBANNED -> TransactionType.INFO
    },
    amount = amount,
    date = createTime,
    description = when (actionType) {
        AccountActionType.OPEN_ACCOUNT -> "Открытие счёта"
        AccountActionType.CLOSE_ACCOUNT -> "Закрытие счёта"
        AccountActionType.TRANSFER_RECEIVED -> "Входящий перевод"
        AccountActionType.TRANSFER_SENT -> "Исходящий перевод"
        AccountActionType.ACCOUNT_BANNED -> "Счёт заблокирован"
        AccountActionType.ACCOUNT_UNBANNED -> "Счёт разблокирован"
    },
    status = when (status) {
        OperationStatus.CREATED -> TransactionStatus.CREATED
        OperationStatus.IN_PROCESS -> TransactionStatus.IN_PROCESS
        OperationStatus.SUCCESS -> TransactionStatus.SUCCESS
        OperationStatus.REJECTED -> TransactionStatus.REJECTED
    },
    fromAccount = if (actionType == AccountActionType.TRANSFER_RECEIVED) accountNumberFrom else null,
    toAccount = if (actionType == AccountActionType.TRANSFER_SENT) recipientAccountNumber else null
)

fun OperationModel.toCreditDomain(fallbackAccountId: String = ""): Transaction = Transaction(
    id = operationId.toString(),
    accountId = accountNumberFrom ?: fallbackAccountId,
    type = when (actionType) {
        AccountActionType.OPEN_ACCOUNT -> TransactionType.CREDIT_TAKEN
        AccountActionType.CLOSE_ACCOUNT -> TransactionType.CREDIT_PAYMENT
        AccountActionType.TRANSFER_RECEIVED -> TransactionType.DEPOSIT
        AccountActionType.TRANSFER_SENT -> TransactionType.CREDIT_PAYMENT
        AccountActionType.ACCOUNT_BANNED -> TransactionType.INFO
        AccountActionType.ACCOUNT_UNBANNED -> TransactionType.INFO
    },
    amount = amount,
    date = createTime,
    description = when (actionType) {
        AccountActionType.OPEN_ACCOUNT -> "Оформление кредита"
        AccountActionType.CLOSE_ACCOUNT -> "Закрытие кредита"
        AccountActionType.TRANSFER_RECEIVED -> "Входящий перевод"
        AccountActionType.TRANSFER_SENT -> "Платеж по кредиту"
        AccountActionType.ACCOUNT_BANNED -> "Кредит заблокирован"
        AccountActionType.ACCOUNT_UNBANNED -> "Кредит разблокирован"
    },
    status = when (status) {
        OperationStatus.CREATED -> TransactionStatus.CREATED
        OperationStatus.IN_PROCESS -> TransactionStatus.IN_PROCESS
        OperationStatus.SUCCESS -> TransactionStatus.SUCCESS
        OperationStatus.REJECTED -> TransactionStatus.REJECTED
    },
    fromAccount = if (actionType == AccountActionType.TRANSFER_RECEIVED) accountNumberFrom else null,
    toAccount = if (actionType == AccountActionType.TRANSFER_SENT) recipientAccountNumber else null
)
