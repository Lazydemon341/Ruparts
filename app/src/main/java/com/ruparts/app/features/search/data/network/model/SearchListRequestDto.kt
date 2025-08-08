package com.ruparts.app.features.search.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.data.network.EndpointRequestDto

class SearchListRequestDto(
    data: SearchListRequestDataDto
) : EndpointRequestDto<SearchListRequestDataDto>(
    action = "mobile.product.list",
    data = data,
)

class SearchListRequestDataDto(
    @SerializedName("_filter")
    val filter: SearchFilter = SearchFilter(),
    @SerializedName("_pagination")
    val pagination: Pagination = Pagination(),
    @SerializedName("_sorting")
    val sorting: Sorting = Sorting(),
) {

    class Sorting(
        @SerializedName("field")
        val field: String? = null,
        @SerializedName("direction")
        val direction: String? = null
    )

    class Pagination(
        @SerializedName("per_page")
        val perPage: Int? = 100,
        @SerializedName("page")
        val page: Int? = 1
    )

    class SearchFilter(
        @SerializedName("search")
        val search: String? = null,

        @SerializedName("flags")
        val flags: List<Long>? = null,

        @SerializedName("location")
        val location: String? = null,

        @SerializedName("sets")
        val sets: List<Long>? = null,
    )
}