package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.sportsdiary.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateCard(
    onDateClick: () -> Unit,
    date: String
) {
    val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val currentDate = remember(date) { LocalDate.parse(date).format(formatter) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.date),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            AnimationIcon(
                onClick = onDateClick,
                icon = painterResource(R.drawable.ic_calendar),
                size = 28.dp,
                selectedContainerColor = Color.Transparent
            )
        }
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}