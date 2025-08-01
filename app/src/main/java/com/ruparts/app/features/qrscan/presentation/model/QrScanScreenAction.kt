package com.ruparts.app.features.qrscan.presentation.model

import com.ruparts.app.features.cart.model.CartListItem

sealed interface QrScanScreenAction {
    data object BackClick : QrScanScreenAction
    data class RemoveItem(val item: CartListItem) : QrScanScreenAction
    data class BarcodesScanned(val barcodes: List<String>) : QrScanScreenAction
    data class ManualInput(val code: String) : QrScanScreenAction
    data object OnTransferToCart : QrScanScreenAction
}