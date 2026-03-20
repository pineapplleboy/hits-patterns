package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.domain.AuthStateStorage
import com.example.g_bankforclient.domain.TokenStorage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.coroutines.resume

class AuthInterceptor(
    private val authStateStorage: AuthStateStorage,
    private val authorizationService: AuthorizationService,
    private val tokenStorage: TokenStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        if (url.contains("/auth/")) {
            return chain.proceed(request)
        }

        val authState: AuthState? = authStateStorage.getAuthState()
        if (authState == null) {
            // Fallback for legacy / transitional cases: use access token if auth state isn't available.
            val token = tokenStorage.getToken()
            if (token != null) {
                val newRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                return chain.proceed(newRequest)
            }
            return chain.proceed(request)
        }

        val freshAccessToken = runBlocking {
            suspendCancellableCoroutine<String?> { continuation ->
                authState.performActionWithFreshTokens(authorizationService) { accessToken, _, ex ->
                    if (ex != null || accessToken.isNullOrBlank()) {
                        continuation.resume(null)
                    } else {
                        continuation.resume(accessToken)
                    }
                }
            }
        }

        // Keep legacy TokenStorage in sync for endpoints that still rely on it.
        if (!freshAccessToken.isNullOrBlank()) {
            tokenStorage.setToken(freshAccessToken)
            authStateStorage.setAuthState(authState)
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $freshAccessToken")
                .build()
            return chain.proceed(newRequest)
        }

        // Fallback: if AppAuth state cannot produce a fresh token for this request,
        // still try last persisted access token.
        val persistedToken = tokenStorage.getToken()
        if (!persistedToken.isNullOrBlank()) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $persistedToken")
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }
}
