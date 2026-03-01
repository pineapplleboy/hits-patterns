package com.example.g_bankforemployees.feature.authorization.domain.repository

import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeLoginInput
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeRegistrationInput
import com.example.g_bankforemployees.feature.authorization.domain.model.RegisterUserInput

interface AuthRepository {

    suspend fun employeeLogin(input: EmployeeLoginInput): Result<String>

    suspend fun employeeRegister(input: EmployeeRegistrationInput): Result<Unit>

    suspend fun registerUser(input: RegisterUserInput): Result<Unit>
}
