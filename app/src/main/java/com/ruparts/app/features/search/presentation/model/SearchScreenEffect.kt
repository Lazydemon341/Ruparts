package com.ruparts.app.features.search.presentation.model

import com.ruparts.app.features.productscan.model.ProductScanType

sealed interface SearchScreenEffect {
    data class NavigateToScan(val scanType: ProductScanType) : SearchScreenEffect
    data class NavigateToProduct(val barcode: String) : SearchScreenEffect
    data object NavigateToAssembly : SearchScreenEffect
}