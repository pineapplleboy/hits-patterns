package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.common.models.User
import com.example.g_bankforclient.common.models.UserRole
import com.example.g_bankforclient.data.network.UserService
import com.example.g_bankforclient.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {
    
    override suspend fun getMyProfile(): User {
        val userDB = userService.getMyProfile()
        return User(
            id = userDB.id,
            name = userDB.name,
            phone = userDB.phone,
            ban = userDB.ban,
            userRole = when (userDB.userRole) {
                com.example.g_bankforclient.data.network.model.UserRole.CLIENT -> UserRole.CLIENT
                com.example.g_bankforclient.data.network.model.UserRole.EMPLOYEE -> UserRole.EMPLOYEE
            },
            author = userDB.author
        )
    }
}
