package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.common.models.UserCredentials
import com.example.g_bankforclient.data.network.AuthService
import com.example.g_bankforclient.data.network.model.UserLoginDTO
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    
    private val _isAuthenticated = MutableStateFlow(false)

    override fun getAuthState(): Flow<Boolean> = _isAuthenticated.asStateFlow()

    override suspend fun login(credentials: UserCredentials): Boolean {
        return try {
            val loginDTO = UserLoginDTO(
                phone = credentials.login,
                password = credentials.password
            )
            val token = authService.clientLogin(loginDTO)
            tokenStorage.setToken(token)
            _isAuthenticated.value = true
            true
        } catch (e: Exception) {
            _isAuthenticated.value = false
            false
        }
    }

    override suspend fun logout() {
        tokenStorage.clearToken()
        _isAuthenticated.value = false
    }
}
