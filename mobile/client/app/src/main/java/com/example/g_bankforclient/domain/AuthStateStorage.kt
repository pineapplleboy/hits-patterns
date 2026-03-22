package com.example.g_bankforclient.domain

import net.openid.appauth.AuthState

interface AuthStateStorage {
    fun getAuthState(): AuthState?
    fun setAuthState(authState: AuthState?)
    fun clearAuthState()
}

