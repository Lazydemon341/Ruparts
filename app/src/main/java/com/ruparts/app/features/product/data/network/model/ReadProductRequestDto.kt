package com.ruparts.app.features.product.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto

class ReadProductRequestDto(data: ReadProductRequestDataDto) :
    EndpointRequestDto<ReadProductRequestDataDto>(
        action = "mobile.product.common.read_product",
        data = data,
    )

class ReadProductRequestDataDto(
    @SerializedName("barcode")
    val barcode: String,
)