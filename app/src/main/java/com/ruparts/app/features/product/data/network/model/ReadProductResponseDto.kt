package com.ruparts.app.features.product.data.network.model

import com.google.gson.annotations.SerializedName

class ReadProductResponseDto(
    @SerializedName("type")
    val type: Int,
    @SerializedName("data")
    val data: ProductDto?,
    @SerializedName("error")
    val error: ReadProductResponseErrorDto?,
)

class ReadProductResponseErrorDto(
    @SerializedName("message")
    val message: String?
)