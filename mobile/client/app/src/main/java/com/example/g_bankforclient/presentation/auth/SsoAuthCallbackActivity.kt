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

            // Persist token response as well, otherwise AuthState.idToken can remain null.
            val authState = AuthState(response, tokenResponse, tokenException)
            authStateStorage.setAuthState(authState)
            tokenStorage.setToken(tokenResponse.accessToken)
            loadProfileAndFinish()
        }
    }

    private fun loadProfileAndFinish() {
        // Now we can load profile (requires Authorization header).
        lifecycleScope.launch(Dispatchers.IO) {
            val ok = runCatching {
                val profile = userService.getMyProfile()
                tokenStorage.setUserId(profile.id.toString())
                true
            }.getOrDefault(false)

            dispatcher.complete(success = ok)
            runCatching { finish() }
        }
    }
}

