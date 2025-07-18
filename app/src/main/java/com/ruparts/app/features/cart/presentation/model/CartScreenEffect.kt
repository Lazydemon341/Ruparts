package com.ruparts.app.features.cart.presentation.model

import com.ruparts.app.features.cart.model.CartListItem

sealed class CartScreenEffect {
    data class OpenTransferToLocationScreen(val scannedItem: CartListItem) : CartScreenEffect()
}
