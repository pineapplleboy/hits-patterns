package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.mapper.toDomain
import com.example.g_bankforclient.data.network.UserService
import com.example.g_bankforclient.domain.models.User
import com.example.g_bankforclient.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {
    
    override suspend fun getMyProfile(): User {
        return userService.getMyProfile().toDomain()
    }
}
