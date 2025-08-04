package com.ruparts.app.features.product.presentation.model

import com.ruparts.app.features.product.domain.Product

sealed interface ProductDetailsScreenState {
    data object Loading : ProductDetailsScreenState
    data object Error : ProductDetailsScreenState
    data class Content(
        val product: Product,
    ) : ProductDetailsScreenState
}