package com.ruparts.app.features.commonlibrary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductFlag(
    val id: Long,
    val title: String,
    val category: String,
) : Parcelable