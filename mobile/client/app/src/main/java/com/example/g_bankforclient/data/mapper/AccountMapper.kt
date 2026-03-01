package com.example.g_bankforclient.data.mapper

import com.example.g_bankforclient.data.network.model.BankAccountFullModel
import com.example.g_bankforclient.data.network.model.BankAccountShortModel
import com.example.g_bankforclient.domain.models.Account

fun BankAccountShortModel.toDomain(): Account = Account(
    id = accountNumber,
    name = "Счет $accountNumber",
    balance = balance,
    banned = banned
)

fun BankAccountFullModel.toDomain(): Account = Account(
    id = accountNumber,
    name = "Счет $accountNumber",
    balance = balance,
    banned = banned
)

