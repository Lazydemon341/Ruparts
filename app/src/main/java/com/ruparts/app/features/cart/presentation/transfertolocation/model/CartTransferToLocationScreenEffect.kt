package com.ruparts.app.features.cart.presentation.transfertolocation.model

sealed interface CartTransferToLocationScreenEffect {
    data class NavigateBack(
        val updateCart: Boolean = false,
        val toastToShow: String? = null,
    ) : CartTransferToLocationScreenEffect

    data class ShowToast(
        val message: String,
    ) : CartTransferToLocationScreenEffect
}