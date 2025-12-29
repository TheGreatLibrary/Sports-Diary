package com.sinya.projects.sportsdiary.presentation.home.calendar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarHeader(today: LocalDate) {
    val locale = remember { Locale.getDefault() }
    val fullFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy", locale) }
    val dayName = today.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).replaceFirstChar { it.titlecase(locale) }

    Text(
        text = "$dayName, ${today.format(fullFormatter)}",
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onPrimary
    )
}