package com.ruparts.app.features.search.data.network.model

import com.google.gson.annotations.SerializedName

class SearchSetItemDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("created")
    val created: String,
)