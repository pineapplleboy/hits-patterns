package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.user.GetMyProfileUseCase
import com.example.g_bankforclient.presentation.state.ProfileScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Loading)
    val state: StateFlow<ProfileScreenState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileScreenState.Loading
            runCatching { getMyProfileUseCase() }
                .onSuccess { user ->
                    _state.value = ProfileScreenState.Default(user = user)
                }
                .onFailure { e ->
                    _state.value = ProfileScreenState.Error(
                        message = e.message ?: "Не удалось загрузить профиль"
                    )
                }
        }
    }
}
