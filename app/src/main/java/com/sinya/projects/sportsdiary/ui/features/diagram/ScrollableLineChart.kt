package com.sinya.projects.sportsdiary.ui.features.diagram

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.domain.model.ChartState
import kotlin.math.roundToInt

@Composable
fun ScrollableLineChart(
    points: List<ChartPoint>,
    timeMode: TypeTime,
    modifier: Modifier = Modifier,
    state: ChartState = ChartState(),
    lineColor: Color,
    gridColor: Color,
    formatYLabel: (Float) -> String = { it.roundToInt().toString() }
) {
    val density = LocalDensity.current
    val paddingPx = with(density) { state.contentPadding.toPx() }
    val xStepPx = with(density) { state.xStep.toPx() }
    val strokeWidthPx = with(density) { state.strokeWidth.toPx() }
    val dotRadiusPx = with(density) { state.dotRadius.toPx() }

    val scrollState = rememberScrollState()
    val chartHeight = remember { 180.dp }
    val yRange = remember(points, timeMode) { (state.yMax - state.yMin).takeIf { it != 0f } ?: 1f }

    val yLabels = remember(points, timeMode) {
        (state.yGridLines downTo 0).map { i ->
            val v = state.yMin + (yRange / state.yGridLines) * i
            formatYLabel(v)
        }
    }
    val groupedLabels = remember(points, timeMode) {
        points.groupPointsByTimeMode(timeMode)
    }
    val labels = remember(points, timeMode) {
        points.parseDateByMode(timeMode)
    }

    BoxWithConstraints(modifier) {
        val desiredContentWidthPx = remember { points.size * xStepPx + paddingPx * 2 }
        val minCanvasWidthPx = remember { constraints.maxWidth.toFloat().coerceAtLeast(0f) }
        val contentWidthPx = remember { desiredContentWidthPx.coerceAtLeast(minCanvasWidthPx) }

        val contentWidthDp = with(density) { contentWidthPx.toDp() }

        val dash = remember { PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f) }

        Row(modifier = Modifier.fillMaxWidth()) {
            YLabelColumn(
                height = chartHeight,
                labels = yLabels
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                ) {
                    Canvas(
                        modifier = Modifier
                            .width(contentWidthDp)
                            .height(chartHeight)
                    ) {
                        val chartRight = size.width - paddingPx
                        val chartBottom = size.height - paddingPx

                        fun xFor(index: Int): Float =
                            paddingPx + index * xStepPx

                        fun yFor(value: Float): Float {
                            val norm = ((value - state.yMin) / yRange).coerceIn(0f, 1f)
                            return chartBottom - norm * (chartBottom - paddingPx)
                        }

                        if (state.yGridLines > 0) {
                            val step = yRange / state.yGridLines
                            for (i in 0..state.yGridLines) {
                                val v = state.yMin + i * step
                                val y = yFor(v)
                                drawLine(
                                    color = gridColor,
                                    start = Offset(paddingPx, y),
                                    end = Offset(chartRight, y),
                                    strokeWidth = 1f,
                                    pathEffect = dash
                                )
                            }
                        }

                        for (i in points.indices) {
                            val x = xFor(i)
                            drawLine(
                                color = gridColor,
                                start = Offset(x, paddingPx),
                                end = Offset(x, chartBottom),
                                strokeWidth = 1f,
                                pathEffect = dash
                            )
                        }

                        val path = Path()
                        path.reset()
                        path.moveTo(xFor(0), yFor(points.first().yValue))
                        for (i in 1 until points.size) {
                            path.lineTo(xFor(i), yFor(points[i].yValue))
                        }

                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(
                                width = strokeWidthPx,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        points.forEachIndexed { i, p ->
                            drawCircle(
                                color = lineColor,
                                radius = dotRadiusPx,
                                center = Offset(xFor(i), yFor(p.yValue))
                            )
                        }
                    }
                }

                XLabelRow(
                    state = state,
                    points = labels,
                    scrollState = scrollState
                )

                XLabelMainRow(
                    state = state,
                    points = groupedLabels,
                    scrollState = scrollState
                )
            }
        }
    }
}

@Composable
private fun YLabelColumn(
    labels: List<String>,
    height: Dp
) {
    Column(
        modifier = Modifier
            .height(height)
            .padding(end = 5.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        labels.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun XLabelRow(
    state: ChartState,
    points: List<String>,
    scrollState: ScrollState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = state.contentPadding)
            .horizontalScroll(scrollState)
    ) {
        points.forEach { item ->
            Box(modifier = Modifier.width(state.xStep)) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun XLabelMainRow(
    state: ChartState,
    points: List<Pair<String, Int>>,
    scrollState: ScrollState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = state.contentPadding)
            .horizontalScroll(scrollState)
    ) {
        points.forEach { (commonPart, count) ->
            Box(modifier = Modifier.width(state.xStep*count)) {
                Text(
                    text = commonPart,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}