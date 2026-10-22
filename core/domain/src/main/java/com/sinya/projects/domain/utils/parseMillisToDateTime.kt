package com.sinya.projects.domain.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun parseMillisToDateTime(millis: Long?): String {
    if (millis == null) return LocalDate.now().toString()

    return try {
        val date = Date(millis)
        return LocalDateTime.ofInstant(
            date.toInstant(),
            ZoneId.systemDefault()
        ).toLocalDate().toString()
    } catch (e: Exception) {
        ""
    }
}