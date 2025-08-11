package com.ruparts.app.features.search.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.cart.data.network.model.CartItemDto

class SearchListResponseDto(
    @SerializedName("data")
    val data: SearchListResponseDataDto
)

class SearchListResponseDataDto(
    @SerializedName("list")
    val list: List<CartItemDto>,
    @SerializedName("_pagination")
    val pagination: Pagination,
)

class Pagination(
    @SerializedName("page")
    val page: Int?,
    @SerializedName("per_page")
    val perPage: Int?,
    @SerializedName("max_page")
    val maxPage: Int?,
    @SerializedName("rows_count")
    val rowsCount: Int?,
)