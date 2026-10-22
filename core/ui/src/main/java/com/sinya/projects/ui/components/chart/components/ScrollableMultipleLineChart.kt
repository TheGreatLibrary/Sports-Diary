package com.sinya.projects.ui.components.chart.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.sinya.projects.ui.components.chart.model.AxisData
import com.sinya.projects.ui.components.chart.model.ChartState
import com.sinya.projects.ui.components.chart.model.SelectedPointInfo
import com.sinya.projects.ui.components.chart.utils.ChartLineDrawer
import com.sinya.projects.ui.components.chart.utils.ChartTouchHandler

@Composable
fun ScrollableMultiLineChart(
    lines: List<com.sinya.projects.domain.model.ChartLine>,
    modifier: Modifier = Modifier,
    state: ChartState = ChartState(),
    gridColor: Color,
    formatYLabel: (Float) -> String,
    axisData: AxisData
) {
    val density = LocalDensity.current
    val paddingPx = with(density) { state.contentPadding.toPx() }
    val xStepPx = with(density) { state.xStep.toPx() }
    val strokeWidthPx = with(density) { state.strokeWidth.toPx() }
    val dotRadiusPx = with(density) { state.dotRadius.toPx() }
    val touchTargetSizePx = with(density) { state.touchTargetSize.toPx() }
    var selectedPoint by remember { mutableStateOf<SelectedPointInfo?>(null) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val scrollState = rememberScrollState()
    val touchHandler = remember { ChartTouchHandler() }
    val totalPoints = axisData.unifiedXAxis.size

    LaunchedEffect(totalPoints) {
        if (totalPoints > 0) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    BoxWithConstraints(modifier) {
        val desiredContentWidthPx = remember(totalPoints) {
            totalPoints * xStepPx + paddingPx * 2
        }
        val minCanvasWidthPx = remember {
            constraints.maxWidth.toFloat().coerceAtLeast(0f)
        }
        val contentWidthPx = remember(desiredContentWidthPx) {
            desiredContentWidthPx.coerceAtLeast(minCanvasWidthPx)
        }
        val contentWidthDp = with(density) { contentWidthPx.toDp() }
        val chartHeight = remember { 180.dp }

        Row(modifier = Modifier.fillMaxWidth()) {
            YLabelColumn(
                height = chartHeight,
                labels = axisData.yLabels
            )
            Box {
                Canvas(
                    modifier = Modifier
                        .width(contentWidthDp)
                        .height(chartHeight)
                        .onGloballyPositioned { coordinates ->
                            canvasSize = coordinates.size.toSize()
                        }
                ) {
                    with(ChartLineDrawer) {
                        drawAxes(0f, gridColor)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    ) {
                        Box {
                            Canvas(
                                modifier = Modifier
                                    .width(contentWidthDp)
                                    .height(chartHeight)
                                    .onGloballyPositioned { coordinates ->
                                        canvasSize = coordinates.size.toSize()
                                    }
                                    .pointerInput(lines) {
                                        detectTapGestures { offset ->
                                            val actualX = offset.x
                                            val actualY = offset.y

                                            val foundPoint = touchHandler.findNearestPoint(
                                                touchX = actualX,
                                                touchY = actualY,
                                                normalizedLines = lines,
                                                canvasHeight = canvasSize.height,
                                                paddingPx = paddingPx,
                                                xStepPx = xStepPx,
                                                yMin = axisData.yMin,
                                                yRange = axisData.yRange,
                                                touchTargetSizePx = touchTargetSizePx,
                                            )

                                            selectedPoint =
                                                if (selectedPoint?.xDate == foundPoint?.xDate &&
                                                    selectedPoint?.lineLabel == foundPoint?.lineLabel
                                                ) {
                                                    null
                                                } else {
                                                    foundPoint
                                                }
                                        }
                                    }
                            ) {
                                canvasSize = size

                                with(ChartLineDrawer) {
                                    drawDataLines(
                                        lines = lines,
                                        paddingPx = paddingPx,
                                        xStepPx = xStepPx,
                                        yMin = axisData.yMin,
                                        yRange = axisData.yRange,
                                        strokeWidthPx = strokeWidthPx,
                                        dotRadiusPx = dotRadiusPx,
                                        showDots = state.showDots,
                                        selectedPoint = selectedPoint
                                    )
                                }
                            }

                            AnimatedChartTooltip(
                                selectedPoint = selectedPoint,
                                formatYLabel = formatYLabel,
                                onDismiss = { selectedPoint = null }
                            )
                        }
                    }

                    XLabelRow(
                        state = state,
                        points = axisData.unifiedXAxis,
                        scrollState = scrollState
                    )
                    axisData.mainUnifiedXAxis?.let {
                        XLabelMainRow(
                            state = state,
                            points = axisData.mainUnifiedXAxis,
                            scrollState = scrollState
                        )
                    }
                }
            }
        }
    }
}

