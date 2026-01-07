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

fun List<ChartPoint>.parseDateByMode(mode: TypeTime) : List<String> {
    return map {
        when(mode) {
            TypeTime.DAYS -> LocalDate.parse(it.xDate).format(DateTimeFormatter.ofPattern("dd"))
            TypeTime.MONTHS -> LocalDate.parse(it.xDate).format(DateTimeFormatter.ofPattern("MM"))
            TypeTime.YEARS -> LocalDate.parse(it.xDate).format(DateTimeFormatter.ofPattern("yyyy"))
        }
    }
}

fun List<ChartPoint>.groupPointsByTimeMode(mode: TypeTime): List<Pair<String, Int>> {
    return when (mode) {
        TypeTime.DAYS -> {
            groupBy { point ->
                val date = LocalDate.parse(point.xDate) // или как ты парсишь дату
                "${date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.year}"
            }.map { (commonPart, group) ->
                Pair(commonPart, group.size)
            }
        }

        TypeTime.MONTHS -> {
            groupBy { point ->
                val date = LocalDate.parse(point.xDate)
                "${date.year} год"
            }.map { (commonPart, group) ->
                Pair(commonPart, group.size)
            }
        }

        TypeTime.YEARS -> {
            groupBy { point ->
                val date = LocalDate.parse(point.xDate)
                val year = date.year
                val decade = "${year / 100 + 1} век" // 2020s, 2030s и т.д.
                decade
            }.map { (commonPart, group) ->
                Pair(commonPart, group.size)
            }
        }
    }
}

