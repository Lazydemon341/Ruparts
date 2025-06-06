package com.ruparts.app.core.utils

import java.time.DateTimeException
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun LocalDate?.formatSafely(formatter: DateTimeFormatter): String? {
    if (this == null) return null

    return try {
        format(formatter)
    } catch (_: DateTimeException) {
        null
    }
}

fun String?.toLocalDate(
    dateFormatter: DateTimeFormatter
): LocalDate? {
    if (this == null) return null
    return try {
        if (this.contains(" ")) {
            val offsetDateTime = OffsetDateTime.parse(this, dateFormatter)
            offsetDateTime.toLocalDate()
        } else {
            LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    } catch (e: DateTimeParseException) {
        null
    }
}