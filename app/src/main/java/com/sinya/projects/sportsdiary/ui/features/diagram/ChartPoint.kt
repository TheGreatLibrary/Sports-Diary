package com.sinya.projects.sportsdiary.ui.features.diagram

import com.sinya.projects.sportsdiary.domain.enums.TypeTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class ChartPoint(
    val xDate: String,   // "Пн", "Вт", "01.09" и т.п.
    val yValue: Float     // 0..100 или что нужно
)

fun ChartPoint.parseDateByMode(mode: TypeTime) : String {
    val daysForm: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
    val monthsForm: DateTimeFormatter = DateTimeFormatter.ofPattern("MM")
    val yearsForm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")

    return when(mode) {
        TypeTime.DAYS -> LocalDate.parse(xDate).format(daysForm)
        TypeTime.MONTHS -> LocalDate.parse(xDate).format(monthsForm)
        TypeTime.YEARS -> LocalDate.parse(xDate).format(yearsForm)
    }
}
fun List<ChartPoint>.groupPointsByTimeMode(
    timeMode: TypeTime
): List<Pair<String, Int>> {
    return when (timeMode) {
        TypeTime.DAYS -> {
            // Группируем по году и месяцу: "2024-12"
            groupBy { point ->
                val date = LocalDate.parse(point.xDate) // или как ты парсишь дату
                "${date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.year}"
            }.map { (commonPart, group) ->
                Pair(commonPart, group.size)
            }
        }

        TypeTime.MONTHS -> {
            // Группируем по году: "2024"
            groupBy { point ->
                val date = LocalDate.parse(point.xDate)
                "${date.year} год"
            }.map { (commonPart, group) ->
                Pair(commonPart, group.size)
            }
        }

        TypeTime.YEARS -> {
            // Группируем по десятилетию или веку
            groupBy { point ->
                val date = LocalDate.parse(point.xDate)
                val year = date.year
                // Например: "2020s" для 2020-2029 или "XXI" для 2000-2099
                val decade = "${year / 100 + 1} век" // 2020s, 2030s и т.д.
                decade
            }.map { (commonPart, group) ->
                Pair(commonPart, group.size)
            }
        }
    }
}

