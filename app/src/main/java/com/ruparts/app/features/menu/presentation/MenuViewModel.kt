package com.ruparts.app.features.menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.authorization.data.repository.AuthRepository
import com.ruparts.app.features.menu.presentation.model.MenuUiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<MenuUiEffect>()
    val uiEffect: SharedFlow<MenuUiEffect> = _uiEffect.asSharedFlow()

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _uiEffect.emit(MenuUiEffect.NavigateToAuth)
    }
}
