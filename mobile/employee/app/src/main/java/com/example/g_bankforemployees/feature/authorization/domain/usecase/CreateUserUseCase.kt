package com.example.g_bankforemployees.feature.authorization.domain.usecase

import com.example.g_bankforemployees.feature.authorization.domain.model.RegisterUserInput
import com.example.g_bankforemployees.feature.authorization.domain.repository.AuthRepository

class CreateUserUseCase(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(input: RegisterUserInput) =
        authRepository.registerUser(input)
}
