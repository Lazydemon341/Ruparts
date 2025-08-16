package com.ruparts.app.features.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.navigation.NavigationManager
import com.ruparts.app.features.authorization.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val navigationManager: NavigationManager,
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<MainUiEffect>()
    val uiEffect: SharedFlow<MainUiEffect> = _uiEffect.asSharedFlow()

    val screenState: StateFlow<MainScreenState> = authRepository.userFlow()
        .map { user ->
            MainScreenState(
                userName = user?.displayName.orEmpty(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainScreenState(""),
        )

    init {
        navigationManager.navigationEvents
            .onEach { event ->
                when (event) {
                    is NavigationManager.NavigationEvent.NavigateToAuth -> {
                        _uiEffect.emit(MainUiEffect.NavigateToAuth(showError = event.showAuthError))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _uiEffect.emit(MainUiEffect.NavigateToAuth(showError = false))
    }
}