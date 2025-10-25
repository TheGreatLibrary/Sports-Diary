package com.sinya.projects.sportsdiary.ui.features.diagram

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.statistic.TimeMode
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.ui.features.diagram.components.ScrollableLineChart
import com.sinya.projects.sportsdiary.ui.features.diagram.model.ChartPoint

@Composable
fun Chart(
    onInfoClick: () -> Unit,
    title: String,
    timeMode: TimeMode,
    points: List<ChartPoint>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            .padding(start = 21.dp, bottom = 21.dp, top = 13.dp, end = 30.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 13.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            AnimationIcon(
                onClick = onInfoClick,
                description = "Info",
                icon = painterResource(R.drawable.ic_info),
                isSelected = true,
                size = 19.dp,
                selectedContainerColor = Color.Transparent,
            )
        }
        ScrollableLineChart(
            points = points,
            timeMode = timeMode,
            yMin = 0f,
            yMax = if (points.isNotEmpty()) points.maxOf { it.yValue } else 5f,
            yGridLines = 4,
            xStep = 80.dp,
            lineColor = MaterialTheme.colorScheme.secondary,
            gridColor = MaterialTheme.colorScheme.onPrimary.copy(0.3f)
        )
    }
}

