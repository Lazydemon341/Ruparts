package com.ruparts.app.features.main.presentation

sealed interface MainUiEffect {
    data class NavigateToAuth(val showError: Boolean) : MainUiEffect
}