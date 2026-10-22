package com.sinya.projects.ui.components.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.components.HeaderWithInfo
import com.sinya.projects.ui.components.chart.components.ChartLegend
import com.sinya.projects.ui.components.chart.components.EmptyChartPlaceholder
import com.sinya.projects.ui.components.chart.components.ScrollableMultiLineChart
import com.sinya.projects.ui.components.chart.model.AxisData
import com.sinya.projects.ui.components.chart.model.ChartState
import com.sinya.projects.ui.components.chart.utils.ChartDataUtils
import com.sinya.projects.ui.components.chart.utils.calculateChartState
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun MultipleChart(
    title: String,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    axisData: AxisData
) {
    val gradients = LocalAppGradients.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = MaterialTheme.shapes.small,
                brush = gradients.basedCardGradient
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderWithInfo(
            title = title,
            onInfoClick = onInfoClick
        )

        if (axisData.normalizedLines.all { it.points.isEmpty() }) EmptyChartPlaceholder()
        else {
            val state = remember { ChartState() }
            val format = remember(axisData) { ChartDataUtils.getSmartFormat(axisData.yMax) }
            val chartState = state.calculateChartState(axisData.normalizedLines)

            ScrollableMultiLineChart(
                lines = axisData.normalizedLines,
                axisData = axisData,
                state = chartState,
                gridColor = MaterialTheme.colorScheme.onPrimary,
                formatYLabel = { format(it) }

            )
        }

        if (!axisData.normalizedLines.all { it.label.isNullOrBlank() }) ChartLegend(
            axisData.normalizedLines
        )
    }
}