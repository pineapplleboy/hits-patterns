package com.example.g_bankforemployees.feature.authorization.data.repository

import com.example.g_bankforemployees.common.network.safeApiCall
import com.example.g_bankforemployees.common.network.safeApiCallUnit
import com.example.g_bankforemployees.feature.authorization.data.mapper.toEmployeeLoginDto
import com.example.g_bankforemployees.feature.authorization.data.mapper.toEmployeeRegisterDto
import com.example.g_bankforemployees.feature.authorization.data.mapper.toRegisterDto
import com.example.g_bankforemployees.feature.authorization.data.remote.AuthApi
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeLoginInput
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeRegistrationInput
import com.example.g_bankforemployees.feature.authorization.domain.model.RegisterUserInput
import com.example.g_bankforemployees.feature.authorization.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun employeeLogin(input: EmployeeLoginInput): Result<String> =
        safeApiCall(
            apiCall = { authApi.employeeLogin(input.toEmployeeLoginDto()) },
            converter = { token ->
                tokenStorage.setToken(token)
                token
            }
        )

    override suspend fun employeeRegister(input: EmployeeRegistrationInput): Result<Unit> =
        safeApiCallUnit { authApi.employeeRegister(input.toEmployeeRegisterDto()) }

    override suspend fun registerUser(input: RegisterUserInput): Result<Unit> =
        safeApiCallUnit { authApi.employeeRegister(input.toRegisterDto()) }
}
