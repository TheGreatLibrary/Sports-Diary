package com.sinya.projects.domain.model

import java.time.LocalDate

data class ProportionsEntry(
    val id: Int = 0,
    val date: String,
    val selected: Boolean = false
)


val List<ProportionsEntry>.years: List<Int>
    get() = this
        .mapNotNull { it.localDateOrNull()?.year }
        .distinct()
        .sortedDescending()



fun List<ProportionsEntry>.monthsForYear(year: Int?): List<Int> {
    return this
        .mapNotNull { proportions ->
            proportions.localDateOrNull()?.takeIf { it.year == year }?.monthValue
        }
        .distinct()
        .sortedByDescending { it }
}

fun List<ProportionsEntry>.filterByYearMonth(year: Int? = null, month: Int? = null): List<ProportionsEntry> {
    return this
        .filter { proportions ->
            proportions.localDateOrNull()?.let { date ->
                // Если year == -1 или null - показываем все годы
                val yearMatches = year == null || year == -1 || date.year == year
                // Если month == -1 или null - показываем все месяцы
                val monthMatches = month == null || month == -1 || date.monthValue == month

                yearMatches && monthMatches
            } ?: false // Если даты нет, не показываем
        }
        .sortedByDescending { it.localDateOrNull() }
}

fun ProportionsEntry.localDateOrNull(): LocalDate? =
    runCatching { LocalDate.parse(date) }.getOrNull()
