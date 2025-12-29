package com.sinya.projects.sportsdiary.presentation.home.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.model.DayOfMonth
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import java.time.LocalDate

@Composable
fun CalendarBody(
    onButtonMorningClick: (LocalDate, Boolean) -> Unit,
    daysNumber: List<DayOfMonth>,
    pickDay: (LocalDate) -> Unit,
    expandedCalendar: Boolean,
    date: LocalDate
) {
    val morningState by remember(daysNumber, date) {
        derivedStateOf {
            daysNumber.fastFirstOrNull { it.date == date }?.morningState ?: false
        }
    }
    val highlightMonth by remember(date) {
        derivedStateOf {
            val currentDate = LocalDate.now()
            date.year == currentDate.year && date.monthValue == currentDate.monthValue && date.dayOfWeek == currentDate.dayOfWeek
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
            .padding(top = 50.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CalendarHeader(today = date)

        WeekdayRow(
            highlightMonth = highlightMonth,
            today = date
        )

        MonthPager(
            days = daysNumber,
            pickDay = pickDay,
            expanded = expandedCalendar,
            today = date
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterHorizontally),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Legend()
            Spacer(Modifier.width(16.dp))
            CustomButton(
                onClick = { onButtonMorningClick(date, !morningState) },
                containerColor = if (!morningState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (!morningState) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondary,
                text = if (!morningState)
                    stringResource(R.string.finish_morning_exercises)
                else
                    stringResource(R.string.finished_morning_exercises)
            )
        }
    }
}