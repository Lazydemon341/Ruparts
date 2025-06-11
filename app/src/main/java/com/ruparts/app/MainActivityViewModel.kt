package com.ruparts.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.model.User
import com.ruparts.app.features.authorization.data.repository.AuthRepository
import com.ruparts.app.features.menu.presentation.model.MenuScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

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

    private fun userFlow(): Flow<User?> {
        return flow {
            val user = authRepository.getUser().getOrNull()
            emit(user)
        }
    }

}