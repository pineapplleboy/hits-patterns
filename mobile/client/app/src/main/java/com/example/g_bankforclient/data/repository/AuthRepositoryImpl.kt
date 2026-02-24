package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.common.models.UserCredentials
import com.example.g_bankforclient.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    
    private val _isAuthenticated = MutableStateFlow(false)

    override fun getAuthState(): Flow<Boolean> = _isAuthenticated.asStateFlow()

    override suspend fun login(credentials: UserCredentials): Boolean {
        val isAuthenticated = credentials.login.isNotEmpty() && credentials.password.isNotEmpty()
        if (isAuthenticated) {
            _isAuthenticated.value = true
        }
        return isAuthenticated
    }

    override suspend fun logout() {
        _isAuthenticated.value = false
    }
}
