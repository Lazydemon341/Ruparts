package com.ruparts.app.features.authorization.presentation.model

data class AuthUiState(
    val pinCode: String = "",
    val isLoading: Boolean = false,
)