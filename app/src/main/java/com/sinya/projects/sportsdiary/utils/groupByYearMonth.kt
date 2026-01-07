package com.sinya.projects.sportsdiary.utils

import java.time.LocalDate
import java.time.Month

fun <T> groupByYearMonth(
    items: List<T>,
    dateExtractor: (T) -> LocalDate? // Лямбда для извлечения даты из объекта
): Map<Int, Map<Month, List<T>>> {

    val dated = items.mapNotNull { item ->
        dateExtractor(item)?.let { date ->
            Triple(item, date.year, date.month)
        }
    }

    return dated
        .groupBy { it.second } // Группировка по году
        .mapValues { (_, triples) ->
            triples.groupBy { it.third } // Группировка по месяцу
                .mapValues { (_, items) -> items.map { it.first } }
        }
        .toSortedMap(compareBy { it }) // Сортировка годов по убыванию
        .mapValues { (_, monthMap) ->
            monthMap.toSortedMap(compareByDescending { it }) // Сортировка месяцев по убыванию
        }
}