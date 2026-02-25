package com.example.g_bankforemployees.feature.authorization.domain.usecase

import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeLoginInput
import com.example.g_bankforemployees.feature.authorization.domain.repository.AuthRepository

class EmployeeLoginUseCase(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(input: EmployeeLoginInput): Result<String> =
        authRepository.employeeLogin(input)
}
