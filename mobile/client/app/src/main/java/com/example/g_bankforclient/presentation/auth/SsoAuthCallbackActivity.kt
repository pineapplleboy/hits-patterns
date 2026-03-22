package com.example.g_bankforclient.presentation.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.g_bankforclient.data.network.UserService
import com.example.g_bankforclient.data.sso.SsoAuthResultDispatcher
import com.example.g_bankforclient.domain.AuthStateStorage
import com.example.g_bankforclient.domain.TokenStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse
import javax.inject.Inject

@AndroidEntryPoint
class SsoAuthCallbackActivity : ComponentActivity() {

    @Inject
    lateinit var dispatcher: SsoAuthResultDispatcher

    @Inject
    lateinit var authStateStorage: AuthStateStorage

    @Inject
    lateinit var tokenStorage: TokenStorage

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var authorizationService: AuthorizationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        if (response == null || error != null) {
            dispatcher.complete(success = false)
            finish()
            return
        }

        // AppAuth might return accessToken directly (if it already performed the token exchange),
        // or it might only return the authorizationCode. Handle both.
        val accessToken = response.accessToken
        if (!accessToken.isNullOrBlank()) {
            val authState = AuthState(response, error)
            authStateStorage.setAuthState(authState)
            tokenStorage.setToken(accessToken)
            loadProfileAndFinish()
            return
        }

        val tokenExchangeRequest = response.createTokenExchangeRequest()
        authorizationService.performTokenRequest(tokenExchangeRequest) { tokenResponse: TokenResponse?, tokenException: AuthorizationException? ->
            if (tokenResponse == null || tokenException != null) {
                dispatcher.complete(success = false)
                finish()
                return@performTokenRequest
            }

            // Сохраняем access token в tokenStorage ДО вызова getMyProfile().
            // AuthState намеренно сохраняем ПОСЛЕ — чтобы AuthInterceptor при вызове
            // getMyProfile() использовал простой путь tokenStorage.getToken()
            // вместо асинхронного performActionWithFreshTokens (который диспатчится
            // на main thread и может создавать timing issues).
            val rawAccessToken = tokenResponse.accessToken
            if (!rawAccessToken.isNullOrBlank()) {
                tokenStorage.setToken(rawAccessToken)
            }

            val authState = AuthState(response, tokenResponse, tokenException)
            loadProfileAndFinish(authState)
        }
    }

    private fun loadProfileAndFinish(authState: AuthState? = null) {
        lifecycleScope.launch(Dispatchers.IO) {
            val ok = runCatching {
                val profile = userService.getMyProfile()
                tokenStorage.setUserId(profile.id.toString())
                // Сохраняем AuthState только после успешной загрузки профиля.
                // С этого момента интерцептор использует полный AppAuth-flow
                // (автообновление токена через performActionWithFreshTokens).
                if (authState != null) {
                    authStateStorage.setAuthState(authState)
                }
                true
            }.getOrDefault(false)

            dispatcher.complete(success = ok)
            runCatching { finish() }
        }
    }
}

