package com.sinya.projects.sportsdiary.ui.features.chart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon

@Composable
fun ChartHeader(
    title: String,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 5.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        AnimationIcon(
            onClick = onInfoClick,
            description = "Info",
            painter = painterResource(R.drawable.ic_info),
            isSelected = true,
            size = 16.dp,
            selectedContainerColor = Color.Transparent,
        )
    }
}