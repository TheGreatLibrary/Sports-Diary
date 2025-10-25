package com.sinya.projects.sportsdiary.ui.features.diagram.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.presentation.statistic.TimeMode
import com.sinya.projects.sportsdiary.presentation.trainings.Training
import com.sinya.projects.sportsdiary.presentation.trainings.components.dateFmt
import com.sinya.projects.sportsdiary.presentation.trainings.components.localDateOrNull
import com.sinya.projects.sportsdiary.ui.features.diagram.model.ChartPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun ScrollableLineChart(
    points: List<ChartPoint>,
    timeMode: TimeMode,
    modifier: Modifier = Modifier,
    yMin: Float = 0f,
    yMax: Float = 100f,
    yGridLines: Int = 6,                  // кол-во горизонтальных линий сетки
    xStep: Dp = 56.dp,                    // ширина «ячейки» по X
    strokeWidth: Dp = 2.dp,
    dotRadius: Dp = 3.5.dp,
    contentPadding: Dp = 5.dp,
    lineColor: Color,
    gridColor: Color,
    labelTextStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onPrimary
    ),
    formatYLabel: (Float) -> String = { it.roundToInt().toString() }
) {
    if (points.isEmpty()) {
        Box(modifier.height(100.dp), contentAlignment = Alignment.Center) {
            Text("Нет данных")
        }
        return
    }

    val scrollState = rememberScrollState()
    val yRange = (yMax - yMin).takeIf { it != 0f } ?: 1f

    val chartHeight = 180.dp
    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val paddingPx = with(density) { contentPadding.toPx() }
        val xStepPx = with(density) { xStep.toPx() }
        val strokeWidthPx = with(density) { strokeWidth.toPx() }
        val dotRadiusPx = with(density) { dotRadius.toPx() }

        // Полная ширина контента для скролла
        val desiredContentWidthPx = (points.size * xStepPx + paddingPx * 2)
        val minCanvasWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(0f)
        val contentWidthPx = desiredContentWidthPx.coerceAtLeast(minCanvasWidthPx)

        val contentWidthDp = with(density) { contentWidthPx.toDp() }

        val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

        Row(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .height(chartHeight)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val labels = (yGridLines downTo 0).map { i ->
                    val v = yMin + (yRange / yGridLines) * i
                    formatYLabel(v)
                }
                labels.forEach {
                    Text(
                        text = it,
                        style = labelTextStyle,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            Column {
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
                        val w = size.width
                        val h = size.height

                        val chartLeft = paddingPx
                        val chartRight = w - paddingPx
                        val chartTop = paddingPx
                        val chartBottom = h - paddingPx

                        // helpers
                        fun xFor(index: Int): Float =
                            chartLeft + index * xStepPx

                        fun yFor(value: Float): Float {
                            val norm = ((value - yMin) / yRange).coerceIn(0f, 1f)
                            // 0 внизу, 1 вверху
                            return chartBottom - norm * (chartBottom - chartTop)
                        }

                        // Горизонтальная пунктирная сетка
                        if (yGridLines > 0) {
                            val step = yRange / yGridLines
                            for (i in 0..yGridLines) {
                                val v = yMin + i * step
                                val y = yFor(v)
                                drawLine(
                                    color = gridColor,
                                    start = Offset(chartLeft, y),
                                    end = Offset(chartRight, y),
                                    strokeWidth = 1f,
                                    pathEffect = dash
                                )
                            }
                        }

                        // Вертикальная пунктирная сетка по X (по каждой «ячейке»)
                        for (i in points.indices) {
                            val x = xFor(i)
                            drawLine(
                                color = gridColor,
                                start = Offset(x, chartTop),
                                end = Offset(x, chartBottom),
                                strokeWidth = 1f,
                                pathEffect = dash
                            )
                        }

                        // Ломаная (Path)
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

                        // Точки
                        points.forEachIndexed { i, p ->
                            drawCircle(
                                color = lineColor,
                                radius = dotRadiusPx,
                                center = Offset(xFor(i), yFor(p.yValue))
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    // Подписи X — скроллятся синхронно с графиком
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(scrollState)
                    ) {
                        Spacer(Modifier.width(contentPadding))
                        points.forEach { p ->
                            Box(
                                modifier = Modifier
                                    .width(xStep)
                                    .padding(vertical = 2.dp),

                            ) {
                                Text(p.parseDateByMode(mode = timeMode), style = labelTextStyle)
                            }
                        }
                        Spacer(Modifier.width(contentPadding))
                    }
                }
            }
        }
    }
}

private fun ChartPoint.parseDateByMode(mode: TimeMode) : String {
    val daysForm: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val monthsForm: DateTimeFormatter = DateTimeFormatter.ofPattern("MM.yyyy")
    val yearsForm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")

    return when(mode.index) {
        0 -> LocalDate.parse(xLabel).format(daysForm)
        1 -> LocalDate.parse(xLabel).format(monthsForm)
        2 -> LocalDate.parse(xLabel).format(yearsForm)
        else -> LocalDate.parse(xLabel).format(daysForm)
    }
}