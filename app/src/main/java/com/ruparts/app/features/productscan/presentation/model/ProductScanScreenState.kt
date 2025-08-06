package com.ruparts.app.features.productscan.presentation.model

import com.ruparts.app.features.productscan.model.ProductScanType

data class ProductScanScreenState(
    val isScanning: Boolean = false,
    val scanType: ProductScanType = ProductScanType.PRODUCT,
)