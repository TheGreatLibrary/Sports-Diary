package com.sinya.projects.sportsdiary.presentation.home.calendar

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.presentation.home.HomeUiState
import java.time.LocalDate
import java.time.YearMonth

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(ym) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, amount -> dragX += amount },
                    onDragEnd = {
                        if (kotlin.math.abs(dragX) > swipeThreshold) {
                            if (dragX < 0) onShift(+1) else onShift(-1)
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
            daysNumber = state.monthDays,
            expandedCalendar = state.calendarExpanded,
            date = state.date
        )

        ExpandButton(
            expanded = state.calendarExpanded,
            onClick = onExtended
        )
    }
}