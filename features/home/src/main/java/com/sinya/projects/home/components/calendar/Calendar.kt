package com.sinya.projects.home.components.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sinya.projects.domain.model.DayOfMonth
import com.sinya.projects.home.components.calendar.components.CalendarBody
import com.sinya.projects.home.components.calendar.components.ExpandButton
import java.time.LocalDate


@Composable
internal fun Calendar(
    today: DayOfMonth,
    days: List<DayOfMonth>,
    calendarExpanded: Boolean,
    onExtended: () -> Unit,
    onShift: (Long) -> Unit,
    onCheckClick: (DayOfMonth) -> Unit,
    onButtonMorningClick: (LocalDate, Boolean) -> Unit,
    pickDay: (LocalDate) -> Unit,
    onToggleListOfMorningDays: () -> Unit,
    navigateToNotes: () -> Unit
) {
    val expanded by rememberUpdatedState(calendarExpanded)

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarBody(
            days = days,
            date = today,
            onButtonMorningClick = onButtonMorningClick,
            expanded = expanded,
            onShift = onShift,
            pickDay = pickDay,
            navigateToNotes = navigateToNotes,
            onToggleListOfMorningDays = onToggleListOfMorningDays,
            onCheckClick = onCheckClick
        )

        ExpandButton(
            expanded = calendarExpanded,
            onClick = onExtended
        )
    }
}