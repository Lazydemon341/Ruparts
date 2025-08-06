package com.ruparts.app.features.search.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class SearchSetItem(
    val id: Long,
    val title: String,
    val author: String,
    val created: LocalDate?,
) : Parcelable
