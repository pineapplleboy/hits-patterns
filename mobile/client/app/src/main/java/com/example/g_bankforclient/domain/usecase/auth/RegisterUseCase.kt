package com.example.g_bankforclient.domain.usecase.auth

import com.example.g_bankforclient.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(name: String, phone: String, password: String): Boolean =
        repository.register(name, phone, password)
}

