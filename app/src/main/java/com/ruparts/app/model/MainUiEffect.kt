package com.ruparts.app.model

sealed interface MainUiEffect {
    data object NavigateToAuth : MainUiEffect
}