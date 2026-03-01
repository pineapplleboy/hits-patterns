package com.example.g_bankforemployees.feature.users_list.domain.model

data class User(
    val id: String,
    val name: String?,
    val phone: String?,
    val ban: Boolean,
    val userRole: UserRole,
    val isBannable: Boolean,
)

enum class UserRole {
    CLIENT,
    EMPLOYEE,
}
