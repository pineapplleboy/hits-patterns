package com.example.g_bankforclient.common.models

data class Credit(
    val id: String,
    val name: String,
    val amount: Double,
    var debt: Double,
    val interestRate: Double
)
