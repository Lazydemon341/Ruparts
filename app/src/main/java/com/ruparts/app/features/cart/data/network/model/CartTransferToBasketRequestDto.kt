package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto

class CartTransferToBasketRequestDto(data: CartTransferToBasketRequestDataDto)  :
    EndpointRequestDto<CartTransferToBasketRequestDataDto>(
        action = "mobile.product.common.transfer_to_basket",
        data = data,
    )


class CartTransferToBasketRequestDataDto(
    @SerializedName("barcodes")
    val barcodes: List<String>

)