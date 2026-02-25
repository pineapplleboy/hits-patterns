package com.example.g_bankforemployees.feature.authorization.domain.model

data class RegisterUserInput(
    val name: String,
    val phone: String,
    val password: String,
    val userRole: String,
)
