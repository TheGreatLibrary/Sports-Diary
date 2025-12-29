package com.sinya.projects.sportsdiary.presentation.home.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.domain.model.DayOfMonth
import java.time.LocalDate

@Composable
fun MonthGrid(
    days: List<DayOfMonth>,
    pickDay: (LocalDate) -> Unit,
    expanded: Boolean,
    today: LocalDate
) {
    val weeks = remember(days) { days.chunked(7) }
    val currentWeekIndex = remember(days, today) {
        weeks.indexOfFirst { row -> row.any { it.date == today } }.let { if (it == -1) 0 else it }
    }
    val rows = if (expanded) weeks else listOf(weeks[currentWeekIndex])

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { dayCell ->
                    val isToday = dayCell.date == today

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(33.dp)
                                .aspectRatio(1f)
                                .background(
                                    shape = MaterialTheme.shapes.extraLarge,
                                    color = if (isToday) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.secondaryContainer
                                )
                                .clickable { pickDay(dayCell.date) }
                                .border(
                                    width = 2.dp,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            if (dayCell.trainingState) MaterialTheme.colorScheme.secondary
                                            else Color.Transparent,
                                            if (dayCell.morningState) MaterialTheme.colorScheme.primary
                                            else Color.Transparent,
                                        ),
                                        start = Offset(0f, 25f),
                                        end = Offset(0f, 100f),
                                        tileMode = TileMode.Clamp
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayCell.date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = when {
                                    !dayCell.currentMonth -> MaterialTheme.colorScheme.onSecondary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

