package com.sinya.projects.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun StatisticCard(
    modifier: Modifier,
    title: String,
    count: String,
    fontSize: TextUnit,
    onTextLayout: ((TextLayoutResult) -> Unit)?
) {
    val gradients = LocalAppGradients.current

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(
                shape = MaterialTheme.shapes.small,
                brush = gradients.basedCard2Gradient
            )
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AdaptiveText(
            text = count,
            style = MaterialTheme.typography.displayLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
            maxLines = 1,
            fontSize = fontSize,
            onTextLayout = onTextLayout
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}