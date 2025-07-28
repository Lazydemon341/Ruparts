package com.ruparts.app.core.utils

import android.text.SpannedString
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import java.util.Locale

fun String.capitalize(): String {
    return replaceFirstChar { firstChar ->
        if (firstChar.isLowerCase()) {
            firstChar.titlecase(Locale("ru", "RU"))
        } else {
            firstChar.toString()
        }
    }
}

fun String.makeBold(boldStartIndex: Int = 0): SpannedString {
    return buildSpannedString {
        append(this@makeBold.substring(0, boldStartIndex))
        bold { append(this@makeBold.substring(boldStartIndex)) }
    }
}