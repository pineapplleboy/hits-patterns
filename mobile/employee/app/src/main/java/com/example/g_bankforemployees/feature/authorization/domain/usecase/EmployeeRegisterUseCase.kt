package com.example.g_bankforemployees.feature.authorization.domain.usecase

import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeRegistrationInput
import com.example.g_bankforemployees.feature.authorization.domain.repository.AuthRepository

class EmployeeRegisterUseCase(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(input: EmployeeRegistrationInput): Result<Unit> =
        authRepository.employeeRegister(input)
}