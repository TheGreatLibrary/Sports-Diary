package com.sinya.projects.sportsdiary.ui.features.diagram

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.domain.model.ChartState
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import kotlin.math.ceil
import kotlin.math.max

@Composable
fun Chart(
    title: String,
    timeMode: TypeTime,
    points: List<ChartPoint>,
    onInfoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                icon = painterResource(R.drawable.ic_info),
                isSelected = true,
                size = 16.dp,
                selectedContainerColor = Color.Transparent,
            )
        }


        if (points.isEmpty()) {
            Box(Modifier.height(100.dp), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.not_found_data))
            }
        }
        else {
            val state = remember {
                ChartState(
                    yMin = 0f,
                    yMax = 5f,
                    yGridLines = 5,
                    xStep = 80.dp,
                )
            }
//            val chartState = remember(points) {
//                state.copy(yMax = points.maxOf { it.yValue } )
//            }

            val scale = buildIntScale(points.maxOf { it.yValue })

            val chartState = state.copy(
                yMax = scale.max.toFloat(),
                yGridLines = scale.lines
            )

            ScrollableLineChart(
                points = points,
                timeMode = timeMode,
                state = chartState,
                lineColor = MaterialTheme.colorScheme.secondary,
                gridColor = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}


data class IntScale(
    val min: Int,
    val max: Int,
    val step: Int,
    val lines: Int
)

fun buildIntScale(maxValue: Float, maxLines: Int = 5): IntScale {
    val maxInt = ceil(maxValue).toInt().coerceAtLeast(1)

    val rawStep = maxInt / maxLines
    val step = max(1, rawStep)

    val lines = ceil(maxInt / step.toFloat()).toInt()

    return IntScale(
        min = 0,
        max = lines * step,
        step = step,
        lines = lines
    )
}