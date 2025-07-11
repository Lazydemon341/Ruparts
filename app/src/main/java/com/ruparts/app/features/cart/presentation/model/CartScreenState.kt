package com.ruparts.app.features.cart.presentation.model

import com.ruparts.app.features.cart.model.CartListItem

data class CartScreenState(
    val items: List<CartListItem>,
    val isLoading: Boolean = false
)
