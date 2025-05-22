package com.ruparts.app.core.data.network

import com.google.gson.annotations.SerializedName

abstract class EndpointRequestDto<T>(
    @SerializedName("id")
    val id: String? = "325ege324ll23el42uicc",
    @SerializedName("action")
    val action: String?,
    @SerializedName("data")
    val data: T?,
)