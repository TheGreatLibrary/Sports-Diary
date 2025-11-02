package com.sinya.projects.sportsdiary.presentation.trainings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.presentation.trainings.Training
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale

val ruLocale = Locale("ru")
val dateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun Training.localDateOrNull(): LocalDate? =
    runCatching { LocalDate.parse(date) }.getOrNull()

@Composable
fun TrainingsByTime(
    trainings: List<Training>,
    onTrainingClick: (Int) -> Unit,
    onMinusClick: (Int) -> Unit,
) {
    val context = LocalContext.current
    val grouped = remember(trainings) {
        groupByYearMonth(trainings) { training ->
            training.localDateOrNull()
        }
    }
    val expandedYear = remember { mutableStateMapOf<Int, Boolean>() }
    val expandedMonth = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        grouped.forEach { (year, monthMap) ->

            item(key = "year-$year-header") {
                val isOpen = expandedYear[year] ?: false
                SectionHeader(
                    title = "$year",
                    expanded = isOpen,
                    style = MaterialTheme.typography.titleMedium,
                    rowFill = 1f,
                    onToggle = { expandedYear[year] = !isOpen }
                )
            }

            monthMap.forEach { (month, monthItems) ->
                val ymKey = "$year-${month.value}"

                item(key = "year-$year-month-${month.value}") {
                    val yearOpen = expandedYear[year] ?: false

                    AnimatedVisibility(
                        visible = yearOpen,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        MonthSection(
                            year = year,
                            month = month,
                            size = monthItems.size,
                            expanded = expandedMonth[ymKey] ?: false,
                            onToggle = { expandedMonth[ymKey] = !(expandedMonth[ymKey] ?: false) },
                        ) {
                            monthItems
                                .sortedByDescending { it.localDateOrNull() }
                                .forEach { t ->
                                    TrainingCard(
                                        t = t,
                                        rowFill = 0.95f,
                                        onTrainingClick = { onTrainingClick(t.id) },
                                        onMinusClick = onMinusClick,
                                        context = context
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}

// Универсальная функция для группировки по году и месяцу
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