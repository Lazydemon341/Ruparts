package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName

class CartScanResponseDto(
    @SerializedName("type")
    val type: Int,
    @SerializedName("data")
    val data: CartScanResponseDataDto?,
    val error: CartScanResponseErrorDto?,
)

class CartScanResponseDataDto(
    @SerializedName("bc_type")
    val bcType: String,
    @SerializedName("scanned")
    val scannedItem: CartItemDto
)

class CartScanResponseErrorDto(
    @SerializedName("message")
    val message: String?
)