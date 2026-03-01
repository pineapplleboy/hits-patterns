package com.example.g_bankforemployees.feature.users_list.data.repository

import com.example.g_bankforemployees.common.network.safeApiCall
import com.example.g_bankforemployees.common.network.safeApiCallUnit
import com.example.g_bankforemployees.feature.users_list.data.mapper.toDomain
import com.example.g_bankforemployees.feature.users_list.data.remote.UsersApi
import com.example.g_bankforemployees.feature.users_list.domain.model.User
import com.example.g_bankforemployees.feature.users_list.domain.repository.UsersRepository

class UsersRepositoryImpl(
    private val usersApi: UsersApi,
) : UsersRepository {

    override suspend fun getMyProfile(): Result<User> =
        safeApiCall(
            apiCall = { usersApi.getMyProfile() },
            converter = { it.toDomain() }
        )

    override suspend fun getUsers(): Result<List<User>> =
        safeApiCall(
            apiCall = { usersApi.getUsers() },
            converter = { list -> list.map { it.toDomain() } }
        )

    override suspend fun banUser(id: String): Result<Unit> =
        safeApiCallUnit { usersApi.banUser(id) }

    override suspend fun unbanUser(id: String): Result<Unit> =
        safeApiCallUnit { usersApi.unbanUser(id) }
}
