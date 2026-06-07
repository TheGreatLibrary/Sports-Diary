package com.sinya.projects.sportsdiary.presentation.home.calendar

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.presentation.home.HomeUiState
import java.time.LocalDate
import java.time.YearMonth
import kotlin.collections.chunked
import kotlin.math.abs

@Composable
fun Calendar(
    state: HomeUiState.Success,
    onExtended: () -> Unit,
    onButtonMorningClick: (LocalDate, Boolean) -> Unit,
    onShift: (Long) -> Unit,
    pickDay: (LocalDate) -> Unit
) {
    val ym = remember(state.date.year, state.date.monthValue) {
        YearMonth.of(
            state.date.year,
            state.date.monthValue
        )
    }
    val swipeThreshold = with(LocalDensity.current) { 56.dp.toPx() }
    var dragX by remember { mutableFloatStateOf(0f) }

    val weeks = remember(state.monthDays) { state.monthDays.chunked(7) }

    var currentWeekIndex by remember(state.monthDays, state.date) {
        mutableIntStateOf(
            weeks.indexOfFirst { row -> row.any { it.date == state.date } }
                .coerceIn(0, weeks.lastIndex)
        )
    }
    LaunchedEffect(state.date) {
        val newIndex = weeks.indexOfFirst { row -> row.any { it.date == state.date } }
        if (newIndex != -1) {
            currentWeekIndex = newIndex
        }
    }

    val expanded by rememberUpdatedState(state.calendarExpanded)

    val rows = if (expanded) weeks else listOf(weeks[currentWeekIndex])

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(ym) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, amount -> dragX += amount },
                    onDragEnd = {
                        if (abs(dragX) > swipeThreshold) {
                            if (expanded) {
                                if (dragX > 0) onShift(-1) else onShift(+1)
                            }
                            else {
                                val newIndex = currentWeekIndex + if (dragX > 0) -1 else +1
                                when {
                                    newIndex < 0 -> onShift(-1)
                                    newIndex >= weeks.size -> onShift(+1)
                                    else -> {
                                        currentWeekIndex = newIndex
                                        weeks[newIndex]
                                            .firstOrNull { it.currentMonth }
                                            ?.let { pickDay(it.date) }
                                    }
                                }
                            }
                        }
                        dragX = 0f
                    },
                    onDragCancel = { dragX = 0f }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarBody(
            onButtonMorningClick = onButtonMorningClick,
            pickDay = pickDay,
            daysNumber = rows,
            date = state.date
        )

        ExpandButton(
            expanded = state.calendarExpanded,
            onClick = onExtended
        )
    }
}