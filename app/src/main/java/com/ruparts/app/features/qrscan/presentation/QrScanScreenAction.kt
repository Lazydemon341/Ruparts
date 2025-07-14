package com.ruparts.app.features.qrscan.presentation

import com.google.mlkit.vision.barcode.common.Barcode
import com.ruparts.app.features.cart.model.CartListItem

sealed interface QrScanScreenAction {
    data object BackClick : QrScanScreenAction
    data class RemoveItem(val item: CartListItem) : QrScanScreenAction
    data class BarcodesScanned(val barcodes: List<Barcode>) : QrScanScreenAction
    data class ManualInput(val code: String) : QrScanScreenAction
}