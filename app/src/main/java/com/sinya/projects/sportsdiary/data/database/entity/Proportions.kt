package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "proportions",
    indices = [
        Index(value = ["date"], orders = [Index.Order.DESC])
    ]
)
data class Proportions(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") val date: String = LocalDate.now().toString()
)

val List<Proportions>.years: List<Int>
    get() = this
        .mapNotNull { it.localDateOrNull()?.year }
        .distinct()
        .sortedDescending()



fun List<Proportions>.monthsForYear(year: Int?): List<Int> {
    return this
        .mapNotNull { proportions ->
            proportions.localDateOrNull()?.takeIf { it.year == year }?.monthValue
        }
        .distinct()
        .sortedByDescending { it }
}

fun List<Proportions>.filterByYearMonth(year: Int? = null, month: Int? = null): List<Proportions> {
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
        .sortedBy { it.localDateOrNull() }
}

fun Proportions.localDateOrNull(): LocalDate? =
    runCatching { LocalDate.parse(date) }.getOrNull()
