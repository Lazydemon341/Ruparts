package com.ruparts.app.features.productscan.presentation.model

sealed class ProductScanScreenEvent {
    data object NavigateBack : ProductScanScreenEvent()
    data class ShowErrorToast(val message: String?) : ProductScanScreenEvent()
    data class NavigateToProductDetails(val productId: String) : ProductScanScreenEvent()
}