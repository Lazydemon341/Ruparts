package com.ruparts.app.core.utils

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