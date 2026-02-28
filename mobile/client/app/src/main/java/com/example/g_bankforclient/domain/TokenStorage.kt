package com.example.g_bankforclient.domain

interface TokenStorage {
    fun getToken(): String?
    fun setToken(token: String?)
    fun clearToken()
}
