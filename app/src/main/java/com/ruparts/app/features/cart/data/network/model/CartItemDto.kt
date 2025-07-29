package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName

class CartItemDto(
    @SerializedName("unit_id")
    val id: Long,
    @SerializedName("vendor_code")
    val vendorCode: String,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("info")
    val info: String?,
    @SerializedName("actions")
    val actions: Actions?,
) {
    class Actions(
        @SerializedName("split")
        val split: Boolean,
        @SerializedName("not_found")
        val notFound: Boolean,
        @SerializedName("reprint")
        val reprint: Boolean,
        @SerializedName("defect")
        val defect: Boolean,
        @SerializedName("suspect_defect")
        val suspectDefect: Boolean,
        @SerializedName("write_off")
        val writeOff: Boolean,

        )
}
