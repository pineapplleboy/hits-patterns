package com.example.g_bankforemployees.common.domain.model

data class Currency(
    val id: Int,
    val name: String,
    val charCode: String,
    val symbol: String,
    val rate: Double,
)
