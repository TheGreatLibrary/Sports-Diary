package com.sinya.projects.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.annotatedText.TextPart
import com.sinya.projects.ui.components.annotatedText.buildAnnotatedText
import com.sinya.projects.ui.theme.LocalDateFormatDMY
import java.time.LocalDate

@Composable
fun DateCard(
    onDateClick: () -> Unit,
    date: String
) {
    val formatter = LocalDateFormatDMY.current
    val currentDate = remember(date) { LocalDate.parse(date).format(formatter) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedText(
                parts = arrayOf(TextPart.Clickable(stringResource(R.string.date_at, currentDate)) { onDateClick() })
            )
        )
        RoundedIcon(
            onClick = onDateClick,
            painter = painterResource(R.drawable.ic_calendar),
            size = 25.dp,
            selectedContainerColor = Color.Transparent
        )
    }
}

