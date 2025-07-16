package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto

class CartTransferToLocationRequestDto(data: CartTransferToLocationRequestDataDto)  :
    EndpointRequestDto<CartTransferToLocationRequestDataDto>(
        action = "mobile.product.common.transfer_to_basket",
        data = data,
    )


class CartTransferToLocationRequestDataDto(
    @SerializedName("barcodes")
    val barcodes: List<String>,
    @SerializedName("location")
    val location: String

)