package com.ruparts.app.features.cart.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartListItem(
    val id: Long,
    val article: String,
    val brand: String,
    val quantity: Int,
    val description: String,
    val barcode: String,
    val cartOwner: String,
    val info: String,
    val fromExternalInput: Boolean = false,
) : Parcelable
