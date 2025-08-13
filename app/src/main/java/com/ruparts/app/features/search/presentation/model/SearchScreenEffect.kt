package com.ruparts.app.features.search.presentation.model

sealed interface SearchScreenEffect {
    data object NavigateToProductScan : SearchScreenEffect
    data class NavigateToProduct(val barcode: String) : SearchScreenEffect
    data object NavigateToLocationScan : SearchScreenEffect
    data object NavigateToAssembly : SearchScreenEffect
}