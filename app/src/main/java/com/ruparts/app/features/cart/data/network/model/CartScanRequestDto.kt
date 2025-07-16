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
    val bcTypes: List<CartScanBCTypeDto>? = listOf(CartScanBCTypeDto.PRODUCT),
    @SerializedName("purpose")
    val purpose: CartScanRequestPurposeDto = CartScanRequestPurposeDto.TRANSFER_TO_BASKET,
)

enum class CartScanRequestPurposeDto {
    @SerializedName("transfer_to_basket")
    TRANSFER_TO_BASKET,
    @SerializedName("transfer_to_location")
    TRANSFER_TO_LOCATION,

    @SerializedName("info")
    INFO,
}

enum class CartScanBCTypeDto {
    @SerializedName("PB")
    PRODUCT,
    @SerializedName("LC")
    LOCATION_CELL,
    @SerializedName("LP")
    LOCATION_PLACE,
}