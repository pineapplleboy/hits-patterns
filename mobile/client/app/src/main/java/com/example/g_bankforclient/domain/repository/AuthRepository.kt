package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.domain.models.UserCredentials

interface AuthRepository {
    suspend fun login(credentials: UserCredentials): Boolean
    suspend fun register(name: String, phone: String, password: String): Boolean
    suspend fun logout()
}
