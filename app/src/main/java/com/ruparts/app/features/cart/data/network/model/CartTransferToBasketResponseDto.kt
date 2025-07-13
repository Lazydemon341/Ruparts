package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName

data class CartTransferToBasketResponseDto(
    @SerializedName("data")
    val data: CartTransferToBasketResponseDataDto
)

data class CartTransferToBasketResponseDataDto(
    @SerializedName("bc_type")
    val bcType: String,
    @SerializedName("scanned")
    val scannedItem : CartItemDto
)