package com.ruparts.app.features.authorization.presentation.model

sealed interface AuthUiAction {
    data class DigitClick(val digit: Int) : AuthUiAction
    data object ClearPin : AuthUiAction
    data object DeleteLastDigit : AuthUiAction
    data object Login : AuthUiAction
}
