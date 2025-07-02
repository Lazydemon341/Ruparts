package com.ruparts.app.features.qrscan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScannedItem (
    val article: String,
    val brand: String,
    val quantity: Int,
    val description: String
) : Parcelable