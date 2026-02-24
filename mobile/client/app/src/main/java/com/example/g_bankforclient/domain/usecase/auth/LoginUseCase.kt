package com.example.g_bankforclient.domain.usecase.auth

import com.example.g_bankforclient.common.models.UserCredentials
import com.example.g_bankforclient.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(credentials: UserCredentials): Boolean = repository.login(credentials)
}
