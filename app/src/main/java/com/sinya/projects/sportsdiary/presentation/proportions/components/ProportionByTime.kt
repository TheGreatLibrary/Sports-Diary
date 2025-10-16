package com.sinya.projects.sportsdiary.presentation.proportions.components

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
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.presentation.proportions.Proportion
import com.sinya.projects.sportsdiary.presentation.trainings.components.MonthSection
import com.sinya.projects.sportsdiary.presentation.trainings.components.SectionHeader
import com.sinya.projects.sportsdiary.presentation.trainings.components.groupByYearMonth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ProportionByTime(
    proportions: List<Proportion>,
    onTrainingClick: (Int) -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") } // один раз

    val grouped = remember(proportions) {
        groupByYearMonth(proportions) { p ->
            runCatching { LocalDate.parse(p.date, formatter) }.getOrNull()
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
                                .sortedBy { runCatching { LocalDate.parse(it.date, formatter) }.getOrNull() }
                                .forEach { t ->
                                    ProportionCard(
                                        t = t,
                                        rowFill = 0.95f,
                                        onTrainingClick = { onTrainingClick(t.id) }
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}