package com.example.g_bankforemployees.feature.authorization.data.repository

import com.example.g_bankforemployees.common.network.safeApiCallUnit
import com.example.g_bankforemployees.feature.authorization.data.mapper.toRegisterUserDto
import com.example.g_bankforemployees.feature.authorization.data.remote.AuthApi
import com.example.g_bankforemployees.feature.authorization.domain.model.RegisterUserInput
import com.example.g_bankforemployees.feature.authorization.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
) : AuthRepository {

    override suspend fun registerUser(input: RegisterUserInput): Result<Unit> =
        safeApiCallUnit { authApi.employeeRegisterUser(input.toRegisterUserDto()) }
}
