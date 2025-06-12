package com.ruparts.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.model.User
import com.ruparts.app.features.authorization.data.repository.AuthRepository
import com.ruparts.app.model.MainUiEffect
import com.ruparts.app.model.MainScreenState
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
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<MainUiEffect>()
    val uiEffect: SharedFlow<MainUiEffect> = _uiEffect.asSharedFlow()

    val screenState: StateFlow<MainScreenState> = userFlow()
        .map { user ->
            MainScreenState(
                userName = user?.displayName.orEmpty(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainScreenState(""),
        )

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _uiEffect.emit(MainUiEffect.NavigateToAuth)
    }

    private fun userFlow(): Flow<User?> {
        return flow {
            val user = authRepository.getUser().getOrNull()
            emit(user)
        }
    }

}