package com.example.g_bankforemployees.feature.authorization.domain.repository

import com.example.g_bankforemployees.feature.authorization.domain.model.RegisterUserInput

interface AuthRepository {

    suspend fun registerUser(input: RegisterUserInput): Result<Unit>
}
