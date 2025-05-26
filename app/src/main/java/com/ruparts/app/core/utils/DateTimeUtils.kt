package com.ruparts.app.core.utils

import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate?.formatSafely(formatter: DateTimeFormatter): String? {
    if (this == null) return null

    return try {
        format(formatter)
    } catch (_: DateTimeException) {
        null
    }
}