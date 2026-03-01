package com.example.g_bankforemployees.feature.user_create.presentation

data class UserCreateScreenState(
    val name: String = "",
    val phone: String = "",
    val password: String = "",
    val roleIndex: Int = 0,
    val isBanned: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val created: Boolean = false,
)
