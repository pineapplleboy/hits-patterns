package com.example.g_bankforemployees.feature.client_details.data.mapper

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.client_details.data.model.BankAccountShortDto
import com.example.g_bankforemployees.feature.client_details.data.model.CreditAccountShortDto

fun BankAccountShortDto.toDomain(): BankAccount = BankAccount(
    id = id,
    accountNumber = accountNumber,
    balance = balance.toDisplayBalanceNumber(),
    balanceText = balance,
    banned = banned,
    hidden = hidden,
)

fun CreditAccountShortDto.toDomain(): CreditAccount = CreditAccount(
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

private fun String.toDisplayBalanceNumber(): Double =
    replace(',', '.')
        .replace(Regex("[^0-9.-]"), "")
        .toDoubleOrNull()
        ?: 0.0
