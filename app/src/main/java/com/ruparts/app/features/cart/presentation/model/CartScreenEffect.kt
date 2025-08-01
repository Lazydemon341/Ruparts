package com.ruparts.app.features.cart.presentation.model

import com.ruparts.app.features.cart.model.CartListItem

sealed interface CartScreenEffect {
    data class OpenTransferToLocationScreen(val scannedItem: CartListItem) : CartScreenEffect
    data class ShowProductScanErrorToast(val message: String?) : CartScreenEffect
}
