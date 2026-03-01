package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.network.AuthService
import com.example.g_bankforclient.data.network.UserService
import com.example.g_bankforclient.data.network.model.RegisterDTO
import com.example.g_bankforclient.data.network.model.UserLoginDTO
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.models.UserCredentials
import com.example.g_bankforclient.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userService: UserService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(credentials: UserCredentials): Boolean {
        val loginDTO = UserLoginDTO(
            phone = credentials.login,
            password = credentials.password
        )
        val token = authService.clientLogin(loginDTO)
        tokenStorage.setToken(token)
        val profile = userService.getMyProfile()
        tokenStorage.setUserId(profile.id.toString())
        return true
    }

    override suspend fun register(name: String, phone: String, password: String): Boolean {
        val registerDTO = RegisterDTO(
            name = name,
            phone = phone,
            password = password
        )
        val token = authService.clientRegister(registerDTO)
        tokenStorage.setToken(token)
        val profile = userService.getMyProfile()
        tokenStorage.setUserId(profile.id.toString())
        return true
    }

    override suspend fun logout() {
        tokenStorage.clearToken()
        tokenStorage.setUserId(null)
    }
}
