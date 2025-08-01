package com.ruparts.app.features.productscan.presentation.model

sealed interface ProductScanScreenAction {
    data object OnBackPressed : ProductScanScreenAction
    data class ManualInput(val input: String) : ProductScanScreenAction
    data class BarcodesScanned(val barcodes: List<String>) : ProductScanScreenAction
}