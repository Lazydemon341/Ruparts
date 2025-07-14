package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName

data class CartScanResponseDto(
    @SerializedName("data")
    val data: CartScanResponseDataDto
)

data class CartScanResponseDataDto(
    @SerializedName("bc_type")
    val bcType: String,
    @SerializedName("scanned")
    val scannedItem : CartItemDto
)