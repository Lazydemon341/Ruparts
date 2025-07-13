package com.ruparts.app.features.cart.data.network.model

import com.google.gson.annotations.SerializedName

data class CartTransferToBasketResponseDto(
    @SerializedName("data")
    val data: CartTransferToBasketResponseDataDto
)

data class CartTransferToBasketResponseDataDto(
    @SerializedName("bc_type")
    val bcType: String,
    @SerializedName("scanned")
    val scannedItem : ScannedItemModel
) {
    class ScannedItemModel(
        @SerializedName("unit_id")
        val unitId: Int,
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
        @SerializedName("flags")
        val flags: List<Int>,
        @SerializedName("info")
        val info: String,
        @SerializedName("actions")
        val actions: ScannedItemActions
    ) {
        class ScannedItemActions(
            @SerializedName("split")
            val split: Boolean,
            @SerializedName("not_found")
            val notFound: Boolean,
            @SerializedName("reprint")
            val reprint: Boolean,
            @SerializedName("defec")
            val defect: Boolean,
            @SerializedName("suspect_defect")
            val suspectDefect: Boolean,
            @SerializedName("write_off")
            val writeOff: Boolean,
        )
    }
}