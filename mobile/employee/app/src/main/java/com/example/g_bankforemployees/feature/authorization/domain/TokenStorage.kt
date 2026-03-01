package com.example.g_bankforemployees.feature.authorization.domain

interface TokenStorage {

    fun getToken(): String?

    fun setToken(token: String?)

    fun clearToken()
}
