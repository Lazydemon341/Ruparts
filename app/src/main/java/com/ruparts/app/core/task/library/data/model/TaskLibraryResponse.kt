package com.ruparts.app.core.task.library.data.model

import com.google.gson.annotations.SerializedName

data class TaskLibraryResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("data")
    val data: TaskLibraryResponseDataDto?,
)

data class TaskLibraryResponseDataDto(
    @SerializedName("implementer")
    val implementer: Map<String, String>?,
)
