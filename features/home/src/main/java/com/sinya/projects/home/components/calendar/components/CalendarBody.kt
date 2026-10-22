package com.sinya.projects.home.components.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.DayOfMonth
import com.sinya.projects.home.R
import com.sinya.projects.ui.components.RoundIconButton
import java.time.LocalDate
import java.time.YearMonth
import kotlin.collections.chunked
import kotlin.math.abs

@Composable
internal fun CalendarBody(
    days: List<DayOfMonth>,
    date: DayOfMonth,
    expanded: Boolean,
    onButtonMorningClick: (LocalDate, Boolean) -> Unit,
    onToggleListOfMorningDays: () -> Unit,
    pickDay: (LocalDate) -> Unit,
    onShift: (Long) -> Unit,
    onCheckClick: (DayOfMonth) -> Unit,
    navigateToNotes: () -> Unit,
) {
    var selectionState by remember { mutableStateOf(false) }

    val highlightMonth by remember(date) {
        derivedStateOf {
            val currentDate = LocalDate.now()
            date.date.year == currentDate.year &&
                    date.date.monthValue == currentDate.monthValue &&
                    date.date.dayOfMonth == currentDate.dayOfMonth
        }
    }
    val ym = remember(date.date.year, date.date.monthValue) {
        YearMonth.of(
            date.date.year,
            date.date.monthValue
        )
    }

    val swipeThreshold = with(LocalDensity.current) { 56.dp.toPx() }
    var dragX by remember { mutableFloatStateOf(0f) }

    var currentWeekIndex by remember(days, date) {
        mutableIntStateOf(days.indexOfFirst { it.date == date.date }
            .coerceIn(0, days.lastIndex) / 7)
    }

    LaunchedEffect(date.date) {
        val newIndex = days.indexOfFirst { it.date == date.date }.coerceIn(0, days.lastIndex) / 7
        if (newIndex != -1) {
            currentWeekIndex = newIndex
        }
    }

    val rows = if (expanded) days else days.chunked(7)[currentWeekIndex]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.medium
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 16.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CalendarHeader(today = date.date)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(ym) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, amount -> dragX += amount },
                        onDragEnd = {
                            if (!selectionState) {
                                if (abs(dragX) > swipeThreshold) {
                                    when (expanded) {
                                        true -> {
                                            if (dragX > 0) onShift(-1) else onShift(+1)
                                        }
                                        else -> {
                                            val newIndex = currentWeekIndex + if (dragX > 0) -1 else +1
                                            when {
                                                newIndex < 0 -> onShift(-1)
                                                newIndex >= (days.size / 7) -> onShift(+1)
                                                else -> {
                                                    currentWeekIndex = newIndex
//                                                    if (days[7 * newIndex].currentMonth) {
                                                        val newCurrentWeek = days.chunked(7)[newIndex]
                                                        val newDay = newCurrentWeek.first { it.date.month == date.date.month }
                                                        pickDay(newDay.date)
//                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                dragX = 0f
                            }
                        },
                        onDragCancel = { dragX = 0f }
                    )
                },
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RowWeek(
                isCurrentMonth = highlightMonth,
                today = date.date
            )

            MonthPager(
                days = rows,
                pickDay = pickDay,
                today = date,
                onCheckClick = onCheckClick,
                onSelectionChange = { selectionState = !selectionState },
                selectionState = selectionState
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Legend()
            Spacer(Modifier.width(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundIconButton(
                    modifier = Modifier,
                    icon = R.drawable.home_notes,
                    onClick = navigateToNotes,
                    buttonSize = 40,
                    iconSize = 24,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                MorningButton(
                    modifier = Modifier,
                    completeIcon = R.drawable.home_morning_complete,
                    defaultIcon = R.drawable.home_morning_default,
                    morningState = date.morningState,
                    onClick = {
                        if (selectionState) onToggleListOfMorningDays() else onButtonMorningClick(
                            date.date,
                            !date.morningState
                        )
                    }
                )
            }
        }
    }
}