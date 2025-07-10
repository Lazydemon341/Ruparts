package com.ruparts.app.features.qrscan.presentation

import com.google.mlkit.vision.barcode.common.Barcode
import com.ruparts.app.features.qrscan.model.ScannedItem

sealed interface QrScanScreenAction {
    data object BackClick : QrScanScreenAction
    data class RemoveItem(val item: ScannedItem) : QrScanScreenAction
    data class BarcodesScanned(val barcodes: List<Barcode>) : QrScanScreenAction
    data object KeyboardClick : QrScanScreenAction
}