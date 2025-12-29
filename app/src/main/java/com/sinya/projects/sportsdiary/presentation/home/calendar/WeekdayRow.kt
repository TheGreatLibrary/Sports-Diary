package com.sinya.projects.sportsdiary.presentation.home.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sinya.projects.sportsdiary.R
import java.time.LocalDate

@Composable
fun WeekdayRow(
    daysTitle: List<String> = listOf(
        stringResource(R.string.monday_short),
        stringResource(R.string.tuesday_short),
        stringResource(R.string.wednesday_short),
        stringResource(R.string.thursday_short),
        stringResource(R.string.friday_short),
        stringResource(R.string.saturday_short),
        stringResource(R.string.sunday_short)
    ),
    highlightMonth: Boolean,
    today: LocalDate
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val dayIndex = today.dayOfWeek.value

        daysTitle.forEachIndexed { i, title ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (highlightMonth && i + 1 == dayIndex)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}
