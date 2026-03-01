package com.example.g_bankforemployees.feature.account_operations.data.mapper

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.account_operations.data.model.BankAccountFullDto
import com.example.g_bankforemployees.feature.account_operations.data.model.CreditAccountFullDto
import com.example.g_bankforemployees.feature.account_operations.data.model.OperationDto
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation

fun BankAccountFullDto.toDomain(): BankAccount = BankAccount(
    id = id,
    accountNumber = accountNumber,
    balance = balance,
    banned = banned,
)

fun CreditAccountFullDto.toDomain(): CreditAccount = CreditAccount(
    id = id,
    accountNumber = accountNumber,
    dept = dept,
    creditRateName = creditRateName,
    creditRatePercent = creditRatePercent,
    writeOffPeriod = writeOffPeriod,
    nextWriteOffDate = nextWriteOffDate,
    banned = banned,
)

fun OperationDto.toDomain(): Operation = Operation(
    operationId = operationId,
    accountNumberFrom = accountNumberFrom,
    userIdFrom = userIdFrom,
    recipientAccountNumber = recipientAccountNumber,
    recipientName = recipientName,
    amount = amount,
    transferAccountType = transferAccountType,
    actionType = actionType,
    status = status,
    createTime = createTime,
)
