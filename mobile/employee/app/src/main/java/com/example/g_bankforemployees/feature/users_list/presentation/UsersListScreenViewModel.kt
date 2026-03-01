package com.example.g_bankforemployees.feature.users_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRate
import com.example.g_bankforemployees.feature.credit_rate.domain.usecase.GetCreditRatesUseCase
import com.example.g_bankforemployees.feature.users_list.domain.model.User
import com.example.g_bankforemployees.feature.users_list.domain.model.UserRole
import com.example.g_bankforemployees.feature.users_list.domain.repository.UsersRepository
import com.example.g_bankforemployees.feature.users_list.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UsersListScreenViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val usersRepository: UsersRepository,
    private val getCreditRatesUseCase: GetCreditRatesUseCase,
    private val tokenStorage: com.example.g_bankforemployees.feature.authorization.domain.TokenStorage,
    private val navigatorHolder: com.example.g_bankforemployees.common.navigation.NavigatorHolder,
) : ViewModel() {

    private val _state: MutableStateFlow<UsersListScreenState> = MutableStateFlow(
        UsersListScreenState.Loading
    )

    val state: StateFlow<UsersListScreenState> = _state.asStateFlow()

    private val _creditRates = MutableStateFlow<List<CreditRate>>(emptyList())
    val creditRates: StateFlow<List<CreditRate>> = _creditRates.asStateFlow()

    private val _tariffsError = MutableStateFlow<String?>(null)
    val tariffsError: StateFlow<String?> = _tariffsError.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = UsersListScreenState.Loading
            getUsersUseCase()
                .onSuccess { users ->
                    val clients = users.filter { it.userRole == UserRole.CLIENT }
                    val employees = users.filter { it.userRole == UserRole.EMPLOYEE }
                    _state.value = UsersListScreenState.Default(
                        clients = clients,
                        employees = employees,
                    )
                }
                .onFailure { e ->
                    _state.value = if (e is HttpException && e.code() == 401) {
                        navigatorHolder.navigator?.navigateToAuthorizationAndClearStack()
                        UsersListScreenState.Loading
                    } else {
                        UsersListScreenState.Error(
                            message = e.message ?: "Не удалось загрузить список пользователей",
                        )
                    }
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

    fun loadTariffs() {
        viewModelScope.launch {
            _tariffsError.value = null
            getCreditRatesUseCase()
                .onSuccess { _creditRates.value = it }
                .onFailure { _tariffsError.value = it.message }
        }
    }

    fun clearTariffsError() {
        _tariffsError.value = null
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

    fun onCreateRateClick() {
        navigatorHolder.navigator?.navigateToCreditRateCreate()
    }

    fun onLogoutClick() {
        tokenStorage.clearToken()
        navigatorHolder.navigator?.navigateToAuthorizationAndClearStack()
    }
}