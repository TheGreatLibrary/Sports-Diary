package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.ui.theme.LocalDateFormat
import java.time.LocalDate

@Composable
fun DateCard(
    onDateClick: () -> Unit,
    date: String
) {
    val formatter = LocalDateFormat.current
    val currentDate = remember(date) { LocalDate.parse(date).format(formatter) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSecondary
        )
        RoundedIcon(
            onClick = onDateClick,
            painter = painterResource(R.drawable.ic_calendar),
            size = 25.dp,
            selectedContainerColor = Color.Transparent
        )
    }
}