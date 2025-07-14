package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto

class CartScanRequestDto(data: CartScanRequestDataDto) :
    EndpointRequestDto<CartScanRequestDataDto>(
        action = "mobile.product.common.scan",
        data = data,
    )

class CartScanRequestDataDto(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("bc_types")
    val bcTypes: List<String>? = null,
    @SerializedName("purpose")
    val purpose: CartScanRequestPurposeDto = CartScanRequestPurposeDto.TRANSFER_TO_BASKET,
)

enum class CartScanRequestPurposeDto {
    @SerializedName("transfer_to_basket")
    TRANSFER_TO_BASKET,

    @SerializedName("info")
    INFO,
}