package com.sinya.projects.domain.model

import java.time.LocalDate

data class NoteItem(
    val id: Int = 0,
    val note: String,
    val date: String,
    val edit: Boolean = false,
    val selected: Boolean = false
)

fun NoteItem.localDateOrNull(): LocalDate? =
    runCatching { LocalDate.parse(date) }.getOrNull()

fun List<NoteItem>.monthsForYear(year: Int?): List<Int> {
    return this
        .mapNotNull { training ->
            training.localDateOrNull()?.takeIf { it.year == year }?.monthValue
        }
        .distinct()
        .sortedByDescending { it }
}

val List<NoteItem>.years: List<Int>
    get() = this
        .mapNotNull { it.localDateOrNull()?.year }
        .distinct()
        .sortedDescending()

fun List<NoteItem>.filterByYearMonth(year: Int? = null, month: Int? = null): List<NoteItem> {
    return this
        .filter { training ->
            training.localDateOrNull()?.let { date ->
                val yearMatches = year == null || year == -1 || date.year == year
                val monthMatches = month == null || month == -1 || date.monthValue == month

                yearMatches && monthMatches
            } ?: false
        }
        .sortedByDescending { it.localDateOrNull() }
}