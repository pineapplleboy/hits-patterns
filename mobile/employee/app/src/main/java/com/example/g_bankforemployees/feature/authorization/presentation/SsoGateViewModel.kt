package com.example.g_bankforemployees.feature.authorization.presentation

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.authorization.domain.AuthSessionCoordinator
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import com.example.g_bankforemployees.feature.authorization.domain.sso.SsoAppAuthConfiguration
import com.example.g_bankforemployees.feature.authorization.domain.sso.SsoConfig
import com.example.g_bankforemployees.feature.notifications.domain.NotificationTokenSyncManager
import com.example.g_bankforemployees.feature.settings.domain.usecase.SyncThemeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.CodeVerifierUtil

class SsoGateViewModel(
    private val tokenStorage: TokenStorage,
    private val context: Context,
    private val authSessionCoordinator: AuthSessionCoordinator,
    private val syncThemeUseCase: SyncThemeUseCase,
    private val notificationTokenSyncManager: NotificationTokenSyncManager,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    private val _state = MutableStateFlow<SsoGateScreenState>(SsoGateScreenState.Default)
    val state: StateFlow<SsoGateScreenState> = _state.asStateFlow()

    private var authorizationLaunched = false
    private var returnedFromSso = false
    private var completingAuthorizedSession = false

    init {
        startSsoIfNeeded()
    }

    fun onRetry() {
        _state.value = SsoGateScreenState.Default
        authorizationLaunched = false
        returnedFromSso = false
        completingAuthorizedSession = false
        authSessionCoordinator.consumePendingSsoError()
        startSsoIfNeeded()
    }

    fun onScreenResumed() {
        if (handleAuthorizedSession()) return

        val pendingError = authSessionCoordinator.consumePendingSsoError()
        if (!pendingError.isNullOrBlank()) {
            authorizationLaunched = false
            returnedFromSso = false
            _state.value = SsoGateScreenState.Error(message = pendingError)
            return
        }

        if (authorizationLaunched && returnedFromSso) {
            authorizationLaunched = false
            returnedFromSso = false
            _state.value = SsoGateScreenState.Error(
                message = "\u0412\u0445\u043e\u0434 \u043d\u0435 \u0437\u0430\u0432\u0435\u0440\u0448\u0451\u043d",
            )
        }
    }

    fun onScreenStopped() {
        if (authorizationLaunched) {
            returnedFromSso = true
        }
    }

    private fun startSsoIfNeeded() {
        if (handleAuthorizedSession()) return
        startSsoAuthorization()
    }

    private fun startSsoAuthorization() {
        _state.value = SsoGateScreenState.Loading
        authorizationLaunched = true
        returnedFromSso = false
        authSessionCoordinator.consumePendingSsoError()

        try {
            val serviceConfig = AuthorizationServiceConfiguration(
                Uri.parse(SsoConfig.AUTHORIZATION_URL),
                Uri.parse(SsoConfig.TOKEN_URL),
            )

            val authRequest = AuthorizationRequest.Builder(
                serviceConfig,
                SsoConfig.CLIENT_ID,
                "code",
                Uri.parse(SsoConfig.REDIRECT_URI),
            )
                .setScopes("SampleAPI")
                .setAdditionalParameters(mapOf("fullAccess" to "true"))
                .setCodeVerifier(CodeVerifierUtil.generateRandomCodeVerifier())
                .build()

            val authorizationService = AuthorizationService(context, SsoAppAuthConfiguration.build())
            val callbackIntent = Intent(context, AuthorizationResultActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                authRequest.hashCode(),
                callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )
            authorizationService.performAuthorizationRequest(authRequest, pendingIntent)
            authorizationService.dispose()
        } catch (throwable: Throwable) {
            authorizationLaunched = false
            _state.value = SsoGateScreenState.Error(
                message = throwable.message ?: "\u041e\u0448\u0438\u0431\u043a\u0430 SSO",
            )
        }
    }

    private fun handleAuthorizedSession(): Boolean {
        if (tokenStorage.getToken().isNullOrBlank()) return false
        if (completingAuthorizedSession) return true

        completingAuthorizedSession = true
        authorizationLaunched = false
        returnedFromSso = false
        authSessionCoordinator.onAuthorized()
        _state.value = SsoGateScreenState.Loading

        notificationTokenSyncManager.register()

        viewModelScope.launch {
            syncThemeUseCase()
                .onSuccess {
                    navigatorHolder.navigator?.navigateToUsersList()
                }
                .onFailure { error ->
                    completingAuthorizedSession = false
                    _state.value = SsoGateScreenState.Error(
                        message = error.message
                            ?: "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u043e\u0434\u0433\u043e\u0442\u043e\u0432\u0438\u0442\u044c \u043f\u0440\u0438\u043b\u043e\u0436\u0435\u043d\u0438\u0435",
                    )
                }
        }
        return true
    }
}
