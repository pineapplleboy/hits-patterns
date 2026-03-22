package com.example.g_bankforemployees.feature.users_list.presentation

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.common.realtime.domain.RealtimeSessionManager
import com.example.g_bankforemployees.feature.authorization.domain.AuthSessionCoordinator
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import com.example.g_bankforemployees.feature.authorization.domain.sso.SsoAppAuthConfiguration
import com.example.g_bankforemployees.feature.authorization.domain.sso.SsoConfig
import com.example.g_bankforemployees.feature.authorization.presentation.LogoutResultActivity
import com.example.g_bankforemployees.feature.users_list.domain.model.User
import com.example.g_bankforemployees.feature.users_list.domain.model.UserRole
import com.example.g_bankforemployees.feature.users_list.domain.repository.UsersRepository
import com.example.g_bankforemployees.feature.users_list.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest

private const val CREATE_EMPLOYEE_URL = "http://91.227.18.176/identity/Account/Create"

class UsersListScreenViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val usersRepository: UsersRepository,
    private val context: Context,
    private val tokenStorage: TokenStorage,
    private val authSessionCoordinator: AuthSessionCoordinator,
    private val realtimeSessionManager: RealtimeSessionManager,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    private val _state = MutableStateFlow<UsersListScreenState>(UsersListScreenState.Loading)
    val state: StateFlow<UsersListScreenState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = UsersListScreenState.Loading
            getUsersUseCase()
                .onSuccess { users ->
                    _state.value = UsersListScreenState.Default(
                        clients = users.filter { it.userRole == UserRole.CLIENT },
                        employees = users.filter { it.userRole == UserRole.EMPLOYEE },
                    )
                }
                .onFailure { error ->
                    _state.value = UsersListScreenState.Error(
                        message = error.message?.takeUnless { it.isBlank() } ?: "РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°РіСЂСѓР·РёС‚СЊ СЃРїРёСЃРѕРє РїРѕР»СЊР·РѕРІР°С‚РµР»РµР№",
                    )
                }
        }
    }

    fun banUser(user: User) {
        viewModelScope.launch {
            usersRepository.banUser(user.id)
            loadUsers()
        }
    }

    fun unbanUser(user: User) {
        viewModelScope.launch {
            usersRepository.unbanUser(user.id)
            loadUsers()
        }
    }

    fun onSelectedUsersTabIndexChange(index: Int) {
        val current = _state.value as? UsersListScreenState.Default ?: return
        _state.value = current.copy(selectedUsersTabIndex = index)
    }

    fun onUserClick(user: User) {
        navigatorHolder.navigator?.navigateToClientDetails(
            userId = user.id,
            userName = user.name.orEmpty(),
            userPhone = user.phone.orEmpty(),
        )
    }

    fun onCreateUserClick() {
        navigatorHolder.navigator?.navigateToUserCreate()
    }

    fun onCreateEmployeeClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CREATE_EMPLOYEE_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun onTariffsClick() {
        navigatorHolder.navigator?.navigateToTariffsList()
    }

    fun onSettingsClick() {
        navigatorHolder.navigator?.navigateToSettings()
    }

    fun onLogoutClick() {
        authSessionCoordinator.onLogoutStarted()
        tokenStorage.clearToken()
        realtimeSessionManager.disconnect()
        navigatorHolder.navigator?.navigateToSsoLoginAndClearStack()

        try {
            val authorizationService = AuthorizationService(context, SsoAppAuthConfiguration.build())
            val serviceConfig = AuthorizationServiceConfiguration(
                Uri.parse(SsoConfig.AUTHORIZATION_URL),
                Uri.parse(SsoConfig.TOKEN_URL),
                null,
                Uri.parse(SsoConfig.END_SESSION_URL),
            )

            val endSessionRequest = EndSessionRequest.Builder(serviceConfig)
                .setPostLogoutRedirectUri(Uri.parse(SsoConfig.POST_LOGOUT_REDIRECT_URI))
                .setState("logout")
                .build()

            val callbackIntent = Intent(context, LogoutResultActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                endSessionRequest.hashCode(),
                callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )

            authorizationService.performEndSessionRequest(endSessionRequest, pendingIntent)
            authorizationService.dispose()
        } catch (_: Throwable) {
            authSessionCoordinator.onLogoutFinished()
        }
    }
}



