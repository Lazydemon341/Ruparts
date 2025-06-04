package com.ruparts.app.core.data.network

import com.google.gson.annotations.SerializedName
import java.util.UUID

abstract class EndpointRequestDto<T>(
    @SerializedName("id")
    val id: String? = UUID.randomUUID().toString(),
    @SerializedName("action")
    val action: String?,
    @SerializedName("data")
    val data: T?,
)