package com.example.g_bankforclient.data.mapper

import com.example.g_bankforclient.data.network.model.UserDTO
import com.example.g_bankforclient.domain.models.User
import com.example.g_bankforclient.domain.models.UserRole
import com.example.g_bankforclient.data.network.model.UserRole as NetworkUserRole

fun UserDTO.toDomain(): User {
    return User(
        id = id,
        name = name,
        phone = phone,
        ban = ban,
        userRole = when (userRole) {
            NetworkUserRole.CLIENT -> UserRole.CLIENT
            NetworkUserRole.EMPLOYEE -> UserRole.EMPLOYEE
        },
        author = author
    )
}

