package com.ruparts.app.features.menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.model.User
import com.ruparts.app.features.authorization.data.repository.AuthRepository
import com.ruparts.app.features.menu.presentation.model.MenuScreenState
import com.ruparts.app.features.menu.presentation.model.MenuUiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<MenuUiEffect>()
    val uiEffect: SharedFlow<MenuUiEffect> = _uiEffect.asSharedFlow()

    val screenState: StateFlow<MenuScreenState> = userFlow()
        .map { user ->
            MenuScreenState(
                userName = user?.displayName.orEmpty(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MenuScreenState(""),
        )

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _uiEffect.emit(MenuUiEffect.NavigateToAuth)
    }

    private fun userFlow(): Flow<User?> {
        return flow {
            val user = authRepository.getUser().getOrNull()
            emit(user)
        }
    }
}
