package com.ruparts.app.features.productscan.presentation.model

sealed class ProductScanScreenAction {
    data object OnBackPressed : ProductScanScreenAction()
    data object OnFlashToggle : ProductScanScreenAction()
    data class OnProductScanned(val barcode: String) : ProductScanScreenAction()
    data class ManualInput(val input: String) : ProductScanScreenAction()
}