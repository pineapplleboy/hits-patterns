package com.example.g_bankforemployees.feature.authorization.data.remote

import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (
            path.endsWith("/auth/employee-login") ||
            path.endsWith("/auth/client-login") ||
            path.endsWith("/auth/employee-register") ||
            path.endsWith("/auth/client-register")
        ) {
            return chain.proceed(request)
        }

        val token = tokenStorage.getToken()
        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}
