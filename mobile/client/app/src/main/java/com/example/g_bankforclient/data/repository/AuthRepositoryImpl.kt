package com.example.g_bankforclient.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.g_bankforclient.data.sso.SsoAuthResultDispatcher
import com.example.g_bankforclient.domain.AuthStateStorage
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.models.UserCredentials
import com.example.g_bankforclient.domain.repository.AuthRepository
import com.example.g_bankforclient.presentation.auth.SsoAuthCallbackActivity
import com.example.g_bankforclient.presentation.auth.SsoLogoutCallbackActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenStorage: TokenStorage,
    private val authStateStorage: AuthStateStorage,
    private val ssoAuthResultDispatcher: SsoAuthResultDispatcher,
    private val authorizationService: AuthorizationService
) : AuthRepository {

    override suspend fun login(credentials: UserCredentials): Boolean {
        // Old phone/password flow is removed on backend; we authenticate via IdentityServer SSO.
        return suspendCancellableCoroutine { continuation ->
            ssoAuthResultDispatcher.setPending(continuation)

            continuation.invokeOnCancellation {
                // Login flow canceled (back press, app closed, etc.)
                ssoAuthResultDispatcher.clearPending(continuation)
            }

            val authEndpoint = Uri.parse("http://91.227.18.176/identity/connect/authorize")
            val tokenEndpoint = Uri.parse("http://91.227.18.176/identity/connect/token")
            val endSessionEndpoint = Uri.parse("http://91.227.18.176/identity/connect/endsession")

            val serviceConfig = AuthorizationServiceConfiguration(
                authEndpoint,
                tokenEndpoint,
                null,
                endSessionEndpoint
            )

            val clientId = "android_client_app"
            val redirectUri = Uri.parse("com.client.android:/callback")

            val scopes = "openid profile SampleAPI offline_access"

            val authRequest = AuthorizationRequest.Builder(
                serviceConfig,
                clientId,
                ResponseTypeValues.CODE,
                redirectUri
            )
                .setScopes(scopes)
                .build()

            val callbackIntent = Intent(context, SsoAuthCallbackActivity::class.java)
            val requestCode = (System.currentTimeMillis() % 100000).toInt()
            val pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            try {
                authorizationService.performAuthorizationRequest(authRequest, pendingIntent)
            } catch (t: Throwable) {
                ssoAuthResultDispatcher.clearPending(continuation)
                continuation.resumeWith(Result.failure(t))
            }
        }
    }

    override suspend fun register(name: String, phone: String, password: String): Boolean {
        // Registration is handled by IdentityServer/SSO.
        // Keep method to satisfy existing architecture.
        return login(UserCredentials(login = phone, password = password))
    }

    override suspend fun logout() {
        // Clear local state immediately so UI treats user as logged out.
        val authState = authStateStorage.getAuthState()
        val idTokenHint = authState?.idToken

        authStateStorage.clearAuthState()
        tokenStorage.clearToken()
        tokenStorage.setUserId(null)

        // Best-effort remote logout (does not block UI).
        val authEndpoint = Uri.parse("http://91.227.18.176/identity/connect/authorize")
        val tokenEndpoint = Uri.parse("http://91.227.18.176/identity/connect/token")
        val endSessionEndpoint = Uri.parse("http://91.227.18.176/identity/connect/endsession")

        val serviceConfig = AuthorizationServiceConfiguration(
            authEndpoint,
            tokenEndpoint,
            null,
            endSessionEndpoint
        )

        if (!idTokenHint.isNullOrBlank()) {
            val logoutRedirectUri = Uri.parse("com.client.android:/logout")
            val endSessionEndpoint = Uri.parse("http://91.227.18.176/identity/connect/endsession")
            val endSessionRequest = EndSessionRequest.Builder(serviceConfig)
                .setIdTokenHint(idTokenHint)
                .setPostLogoutRedirectUri(logoutRedirectUri)
                .build()

            val callbackIntent = Intent(context, SsoLogoutCallbackActivity::class.java)
            val requestCode = (System.currentTimeMillis() % 100000).toInt()
            val pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            runCatching {
                authorizationService.performEndSessionRequest(endSessionRequest, pendingIntent)
            }
        }
    }
}
