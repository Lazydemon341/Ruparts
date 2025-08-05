package com.ruparts.app.features.commonlibrary.presentation

import com.ruparts.app.R
import com.ruparts.app.features.commonlibrary.ProductFlag

fun ProductFlag.getIconRes(): Int? {
    return when (id) {
        1L -> R.drawable.ruler
        2L -> R.drawable.weight
        4L -> R.drawable.photo
        8589934592L -> R.drawable.recycling
        562949953421312L -> R.drawable.build
        else -> null
    }
}