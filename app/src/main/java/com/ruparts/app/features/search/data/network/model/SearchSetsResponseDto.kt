package com.ruparts.app.features.search.data.network.model

import com.google.gson.annotations.SerializedName

class SearchSetsResponseDto(
    @SerializedName("data")
    val data: List<SearchSetItemDto>
)