package com.example.g_bankforemployees.feature.client_details.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BankAccountShortDto(
    val id: String,
    val accountNumber: String,
    val balance: Double,
    val banned: Boolean = false,
)
