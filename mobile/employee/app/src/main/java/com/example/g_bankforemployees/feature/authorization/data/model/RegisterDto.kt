package com.example.g_bankforemployees.feature.authorization.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val name: String,
    val phone: String,
    val password: String,
)

