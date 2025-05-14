package com.ruparts.app.features.menu.presentation.model

sealed interface MenuUiEffect {
    data object NavigateToAuth : MenuUiEffect
}
