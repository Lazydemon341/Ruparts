package com.ruparts.app.model

sealed interface MainUiEffect {
    data class NavigateToAuth(val showError: Boolean) : MainUiEffect
}