package com.example.g_bankforemployees.feature.authorization.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeLoginDto(
    val phone: String,
    val password: String,
)
