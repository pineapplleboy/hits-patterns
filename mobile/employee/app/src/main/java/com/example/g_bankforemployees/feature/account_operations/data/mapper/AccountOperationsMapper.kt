package com.example.g_bankforemployees.feature.account_operations.data.mapper

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.common.domain.model.Currency
import com.example.g_bankforemployees.feature.account_operations.data.model.BankAccountFullDto
import com.example.g_bankforemployees.feature.account_operations.data.model.CreditAccountFullDto
import com.example.g_bankforemployees.feature.account_operations.data.model.CurrencyDto
import com.example.g_bankforemployees.feature.account_operations.data.model.OperationDto
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation

fun BankAccountFullDto.toDomain(): BankAccount = BankAccount(
    id = id,
    accountNumber = accountNumber,
    balance = balance,
    balanceText = null,
    banned = banned,
    hidden = false,
    createTime = createTime,
    currency = currency?.toDomain(),
)

fun CreditAccountFullDto.toDomain(): CreditAccount = CreditAccount(
    id = id,
    accountNumber = accountNumber,
    dept = dept.toDisplayBalanceNumber(),
    deptText = dept,
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
    recipientName = null,
    amount = amount,
    transferAccountType = transferAccountType,
    actionType = actionType,
    status = status,
    createTime = createTime,
)

private fun CurrencyDto.toDomain(): Currency = Currency(
    id = id,
    name = name,
    charCode = charCode,
    symbol = symbol,
    rate = rate,
)

private fun String.toDisplayBalanceNumber(): Double =
    replace(',', '.')
        .replace(Regex("[^0-9.-]"), "")
        .toDoubleOrNull()
        ?: 0.0
