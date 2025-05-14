package com.ruparts.app.features.authorization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.authorization.data.repository.AuthRepository
import com.ruparts.app.features.authorization.presentation.model.AuthUiAction
import com.ruparts.app.features.authorization.presentation.model.AuthUiEffect
import com.ruparts.app.features.authorization.presentation.model.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AuthUiEffect>()
    val uiEffect: SharedFlow<AuthUiEffect> = _uiEffect.asSharedFlow()
        .onSubscription {
            if (authRepository.isAuthenticated()) {
                emit(AuthUiEffect.NavigateToMenu)
            }
        }

    fun handleAction(action: AuthUiAction) {
        when (action) {
            is AuthUiAction.DigitPressed -> handleDigitPressed(action.digit)
            is AuthUiAction.ClearPin -> handleClearPin()
            is AuthUiAction.DeleteLastDigit -> handleDeleteLastDigit()
            is AuthUiAction.Login -> handleLogin()
        }
    }

    private fun handleDigitPressed(digit: Int) {
        if (_uiState.value.isLoading) return

        val currentPin = _uiState.value.pinCode
        if (currentPin.length < 6) {
            _uiState.update { currentState ->
                currentState.copy(pinCode = currentPin + digit.toString())
            }

            if (currentPin.length + 1 == 6) {
                handleLogin()
            }
        }
    }

    private fun handleClearPin() {
        if (_uiState.value.isLoading) return

        _uiState.update { currentState ->
            currentState.copy(pinCode = "")
        }
    }

    private fun handleDeleteLastDigit() {
        if (_uiState.value.isLoading) return

        val currentPin = _uiState.value.pinCode
        if (currentPin.isNotEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(pinCode = currentPin.dropLast(1))
            }
        }
    }

    private fun handleLogin() {
        val pinCode = _uiState.value.pinCode
        if (pinCode.length != 6) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            authRepository.loginWithPinCode(pinCode).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.emit(AuthUiEffect.NavigateToMenu)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, pinCode = "") }
                    _uiEffect.emit(AuthUiEffect.ShowError(error))
                }
            )
        }
    }
}
