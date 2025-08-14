package com.ruparts.app.features.cart.model

import android.os.Parcelable
import com.ruparts.app.features.commonlibrary.ProductFlag
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartOwner(
    val type: OwnerType,
    val text: String
) : Parcelable

enum class OwnerType {
    Location,
    Cart
}

@Parcelize
data class CartListItem(
    val id: Long,
    val article: String,
    val brand: String,
    val quantity: Int,
    val description: String,
    val barcode: String,
    val cartOwner: CartOwner,
    val info: String,
    val flags: List<ProductFlag> = emptyList(),
    val fromExternalInput: Boolean = false,
) : Parcelable
