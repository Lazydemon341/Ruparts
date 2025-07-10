package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName

data class CartResponseDto(
    @SerializedName("data")
    val data: CartResponseDataDto?,
)

data class CartResponseDataDto(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("items")
    val items: List<CartItemDto>,
    @SerializedName("actions")
    val actions: Actions
) {
    class Actions(
        @SerializedName("add_item")
        val addItem: Boolean,
        @SerializedName("remove_item")
        val removeItem: Boolean
    )
}
