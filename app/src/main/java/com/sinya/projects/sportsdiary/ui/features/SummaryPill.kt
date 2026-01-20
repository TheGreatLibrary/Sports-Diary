package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryPill(
    text: AnnotatedString,
    unit: String,
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Text(
            text = unit,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
