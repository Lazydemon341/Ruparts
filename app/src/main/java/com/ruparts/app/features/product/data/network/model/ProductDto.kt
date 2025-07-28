package com.ruparts.app.features.product.data.network.model

import com.google.gson.annotations.SerializedName

class ProductDto(
    @SerializedName("unit_id") val unitId: Int,
    @SerializedName("vendor_code") val vendorCode: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("description") val description: String?,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("barcode") val barcode: String,
    @SerializedName("location") val location: String,
    @SerializedName("accepted_at") val acceptedAt: String?,
    @SerializedName("unit_comment") val unitComment: String?,
    @SerializedName("flags") val flags: List<Long>?,
    @SerializedName("photos") val photos: Map<String, String>?,
    @SerializedName("card") val card: ProductCardDto?,
    @SerializedName("defect") val defect: ProductDefectDto?,
    @SerializedName("actions") val actions: ProductActionsDto?
)

class ProductCardDto(
    @SerializedName("id") val id: Int,
    @SerializedName("weight") val weight: Int?,
    @SerializedName("size_height") val sizeHeight: Int?,
    @SerializedName("size_width") val sizeWidth: Int?,
    @SerializedName("size_length") val sizeLength: Int?,
    @SerializedName("comment") val comment: String?,
    @SerializedName("flags") val flags: List<Long>?,
    @SerializedName("photos") val photos: Map<String, String>?
)

class ProductDefectDto(
    @SerializedName("comment") val comment: String?,
    @SerializedName("photos") val photos: Map<String, String>?
)

class ProductActionsDto(
    @SerializedName("split") val split: Boolean?,
    @SerializedName("not_found") val notFound: Boolean?,
    @SerializedName("reprint") val reprint: Boolean?,
    @SerializedName("defect") val defect: Boolean?,
    @SerializedName("suspect_defect") val suspectDefect: Boolean?,
    @SerializedName("write_off") val writeOff: Boolean?
)