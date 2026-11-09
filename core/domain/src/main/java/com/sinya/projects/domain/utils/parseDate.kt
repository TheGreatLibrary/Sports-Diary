package com.sinya.projects.domain.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun parseDateToStringUI(date: LocalDateTime): String {
    return runCatching { date.format(BG_DATE_TIME) }.getOrElse { date.toString() }
}

fun parseDateToStringUI(date: LocalDate): String {
    return runCatching { date.format(BG_DATE) }.getOrElse { date.toString() }
}
fun parseStringToDate(date: String): LocalDateTime {
    return runCatching { LocalDateTime.parse(date, DB_DATE_TIME) }
        .getOrElse { LocalDate.parse(date).atStartOfDay() }
}

fun parseDateToStringDB(date: LocalDateTime): String = date.format(DB_DATE_TIME)

fun parseStringDateToStringUI(date: String): String {
    return  parseDateToStringUI(LocalDateTime.parse(date, DB_DATE_TIME))
}

private val DB_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private val BG_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
private val BG_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy")