package com.ruparts.app.features.productscan.presentation.model

data class ProductScanScreenState(
    val isFlashEnabled: Boolean = false,
    val isScanning: Boolean = true,
    val scannedData: String? = null
)