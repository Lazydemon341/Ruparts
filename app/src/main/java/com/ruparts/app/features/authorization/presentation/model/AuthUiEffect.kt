package com.ruparts.app.features.authorization.presentation.model

sealed interface AuthUiEffect {
    data object NavigateToMenu : AuthUiEffect
    data class ShowError(val throwable: Throwable) : AuthUiEffect
}
