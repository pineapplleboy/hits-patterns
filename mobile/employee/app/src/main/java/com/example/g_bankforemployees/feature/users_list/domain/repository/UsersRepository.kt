package com.example.g_bankforemployees.feature.users_list.domain.repository

import com.example.g_bankforemployees.feature.users_list.domain.model.User

interface UsersRepository {

    suspend fun getMyProfile(): Result<User>

    suspend fun getUsers(): Result<List<User>>

    suspend fun banUser(id: String): Result<Unit>

    suspend fun unbanUser(id: String): Result<Unit>
}
