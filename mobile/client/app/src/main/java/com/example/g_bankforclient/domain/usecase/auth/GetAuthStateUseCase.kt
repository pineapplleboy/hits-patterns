package com.example.g_bankforclient.domain.usecase.auth

import com.example.g_bankforclient.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.getAuthState()
}
