package com.example.g_bankforemployees.feature.authorization.data.remote

import android.os.Handler
import android.os.Looper
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.common.realtime.domain.RealtimeSessionManager
import com.example.g_bankforemployees.feature.authorization.domain.AuthSessionCoordinator
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage,
    private val authSessionCoordinator: AuthSessionCoordinator,
    private val navigatorHolder: NavigatorHolder,
    private val realtimeSessionManager: RealtimeSessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenStorage.getToken()
        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        val response = chain.proceed(newRequest)
        if (
            response.code == 401 &&
            request.url.encodedPath.startsWith("/notification/").not() &&
            authSessionCoordinator.tryStartUnauthorizedRedirect()
        ) {
            tokenStorage.clearToken()
            realtimeSessionManager.disconnect()
            Handler(Looper.getMainLooper()).post {
                navigatorHolder.navigator?.navigateToSsoLoginAndClearStack()
            }
        }
        return response
    }
}

