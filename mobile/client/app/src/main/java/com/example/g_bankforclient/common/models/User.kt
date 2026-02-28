package com.example.g_bankforclient.common.models

import java.util.UUID

data class User(
    val id: UUID,
    val name: String?,
    val phone: String?,
    val ban: Boolean,
    val userRole: UserRole,
    val author: UUID?
)

enum class UserRole {
    CLIENT,
    EMPLOYEE
}
