package com.sinya.projects.sportsdiary.domain.model

import java.time.LocalDate

data class Training(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val category: String,
    val date: String
)

val List<Training>.years: List<Int>
    get() = this
        .mapNotNull { it.localDateOrNull()?.year }
        .distinct()
        .sortedDescending()

val List<Training>.categories: List<String>
    get() = this
        .map { it.category }
        .distinct()
        .sortedDescending()

fun List<Training>.monthsForYear(year: Int?): List<Int> {
    return this
        .mapNotNull { training ->
            training.localDateOrNull()?.takeIf { it.year == year }?.monthValue
        }
        .distinct()
        .sortedByDescending { it }
}

fun List<Training>.filterByYearMonth(year: Int? = null, month: Int? = null): List<Training> {
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

fun List<Training>.filterByMuscle(category: String?): List<Training> {
    return this
        .filter { training ->
            category.isNullOrEmpty() || training.category == category
        }
        .sortedByDescending { it.localDateOrNull() }
}

fun Training.localDateOrNull(): LocalDate? =
    runCatching { LocalDate.parse(date) }.getOrNull()
