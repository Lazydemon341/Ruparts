package com.ruparts.app.features.cart.model

data class CartListItem(
    val article: String,
    val brand: String,
    val amount: Int,
    val description: String,
    val barcode: String,
    val cartOwner: String
)


