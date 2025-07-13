package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto

class CartTransferToBasketRequestDto(data: CartTransferToBasketRequestDataDto) :
    EndpointRequestDto<CartTransferToBasketRequestDataDto>(
    action = "mobile.product.common.scan",
    data = data,
)

class CartTransferToBasketRequestDataDto(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("bc_types")
    val bcTypes: List<String>,
    @SerializedName("purpose")
    val purpose: String,
)