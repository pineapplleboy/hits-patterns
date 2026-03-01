package com.example.g_bankforclient.domain.models

data class Account(
    val id: String,
    val name: String,
    var balance: Double,
    val banned: Boolean = false
)

