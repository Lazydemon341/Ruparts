package com.ruparts.app.features.commonlibrary.data.network.model

import com.google.gson.annotations.SerializedName

class GetLibraryResponseDto(
    @SerializedName("type")
    val type: Int,
    @SerializedName("data")
    val data: GetLibraryResponseDataDto?
)

class GetLibraryResponseDataDto(
    @SerializedName("context")
    val context: ContextDto?
)

class ContextDto(
    @SerializedName("wms")
    val wms: WmsDto?
)

class WmsDto(
    @SerializedName("wms_product_flag")
    val wmsProductFlag: List<WmsProductFlagDto>?
)

class WmsProductFlagDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("category")
    val category: WmsProductFlagCategory,
)

enum class WmsProductFlagCategory {
    @SerializedName("COMMON")
    COMMON,

    @SerializedName("PRODUCT_CARD")
    PRODUCT_CARD,

    @SerializedName("PRODUCT_UNIT")
    PRODUCT_UNIT,

    @SerializedName("PRODUCT_FLAW")
    PRODUCT_FLAW,

    UNKNOWN,
}
