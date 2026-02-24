package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.common.models.UserCredentials
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getAuthState(): Flow<Boolean>
    suspend fun login(credentials: UserCredentials): Boolean
    suspend fun logout()
}
