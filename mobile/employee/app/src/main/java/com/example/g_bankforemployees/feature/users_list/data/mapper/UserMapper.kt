package com.example.g_bankforemployees.feature.users_list.data.mapper

import com.example.g_bankforemployees.feature.users_list.data.model.UserDto
import com.example.g_bankforemployees.feature.users_list.domain.model.User
import com.example.g_bankforemployees.feature.users_list.domain.model.UserRole

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    phone = phone,
    ban = ban,
    userRole = when (userRole.uppercase()) {
        "CLIENT" -> UserRole.CLIENT
        "EMPLOYEE" -> UserRole.EMPLOYEE
        else -> UserRole.CLIENT
    },
    isBannable = isBannable,
)
