package com.example.g_bankforemployees.feature.authorization.presentation

import android.app.Activity
import android.os.Bundle
import com.example.g_bankforemployees.feature.authorization.domain.AuthSessionCoordinator
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import com.example.g_bankforemployees.feature.authorization.domain.sso.SsoAppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthorizationResultActivity : Activity(), KoinComponent {

    private val authSessionCoordinator: AuthSessionCoordinator by inject()
    private val tokenStorage: TokenStorage by inject()
    private var authorizationService: AuthorizationService? = null

    private fun disposeAuthorizationService() {
        authorizationService?.dispose()
        authorizationService = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authorizationService = AuthorizationService(
            applicationContext,
            SsoAppAuthConfiguration.build(),
        )

        val authorizationResponse = AuthorizationResponse.fromIntent(intent)
        val authorizationException = AuthorizationException.fromIntent(intent)

        if (authorizationException != null) {
            publishError(authorizationException.errorDescription ?: authorizationException.message)
            return
        }

        if (authorizationResponse == null) {
            publishError("SSO authorization failed")
            return
        }

        val tokenExchangeRequest = authorizationResponse.createTokenExchangeRequest()
        authorizationService?.performTokenRequest(tokenExchangeRequest) { tokenResponse: TokenResponse?, tokenException: AuthorizationException? ->
            val accessToken = tokenResponse?.accessToken
            if (!accessToken.isNullOrBlank()) {
                tokenStorage.setToken(accessToken)
                authSessionCoordinator.onAuthorized()
            } else {
                publishError(
                    tokenException?.errorDescription
                        ?: tokenException?.message
                        ?: "Token exchange failed",
                )
                return@performTokenRequest
            }
            disposeAuthorizationService()
            finish()
        }
    }

    override fun onDestroy() {
        disposeAuthorizationService()
        super.onDestroy()
    }

    private fun publishError(message: String?) {
        authSessionCoordinator.setPendingSsoError(message ?: "SSO authorization failed")
        disposeAuthorizationService()
        finish()
    }
}
