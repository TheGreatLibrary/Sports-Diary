package com.sinya.projects.domain.model

import android.util.Log
import com.sinya.projects.domain.utils.parseStringToDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Training(
    val id: Int,
    val name: String,
    val categoryId: Int?,
    val category: String?,
    val date: String,
    val selected: Boolean = false
)

val List<Training>.years: List<Int>
    get() = this
        .mapNotNull { it.localDateOrNull()?.year }
        .distinct()
        .sortedDescending()

val List<Training>.categories: List<String?>
    get() = this
        .map { it.category }
        .distinct()
        .sortedWith(
            compareBy<String?> { it == null }
                .thenByDescending { it }
        )

fun List<Training>.monthsForYear(year: Int?): List<Int> {
    return this
        .mapNotNull { training ->
            training.localDateOrNull()?.takeIf { it.year == year }?.monthValue
        }
        .distinct()
        .sortedByDescending { it }
}

fun List<Training>.filterByYearMonth(year: Int? = null, month: Int? = null): List<Training> {
    Log.d("Trainnn", this.toString())
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
            category.isNullOrEmpty() || training.category == category || (category == "no one" && training.category == null)
        }
        .sortedByDescending { it.localDateOrNull() }
}
fun Training.localDateOrNull(): LocalDateTime? = parseStringToDate(date)
