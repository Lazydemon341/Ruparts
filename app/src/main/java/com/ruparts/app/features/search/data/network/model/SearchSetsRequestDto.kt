package com.ruparts.app.features.search.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto

class SearchSetsRequestDto(
    data: SearchSetsRequestDataDto
) : EndpointRequestDto<SearchSetsRequestDataDto>(
    action = "mobile.product.list.search_sets",
    data = data,
)

class SearchSetsRequestDataDto(
    @SerializedName("search")
    val search: String = "импорт"
)