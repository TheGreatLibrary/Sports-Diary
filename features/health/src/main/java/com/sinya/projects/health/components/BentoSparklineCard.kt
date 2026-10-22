package com.sinya.projects.health.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun BentoSparklineCard(
    title: String,
    subtitle: String,
    mainValue: String,
    hourlyValues: List<Float>,
    barColor: Color,
    gradient: Brush,
    mutedColor: Color,
    modifier: Modifier = Modifier
) {
    ParamHealthCard(
        modifier = modifier,
        gradient = gradient,
        verticalArr = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Text(
            text = mainValue,
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {
                val barsCount = 24
                val spacePerBar = size.width / barsCount
                val strokeW = 2.5.dp.toPx()
                val nonZero = hourlyValues.filter { it > 0f }
                val minVal = (nonZero.minOrNull() ?: 0f) * 0.85f
                val maxVal = (nonZero.maxOrNull() ?: 100f) * 1.05f
                val range = (maxVal - minVal).coerceAtLeast(1f)

                for (i in 0 until barsCount) {
                    val x = i * spacePerBar + spacePerBar / 2f
                    val raw = hourlyValues.getOrElse(i) { 0f }

                    if (raw > 0f) {
                        val norm = ((raw - minVal) / range).coerceIn(0.2f, 1f)
                        val barHeight = size.height * norm
                        val topOffset = (size.height - barHeight) / 2f
                        drawLine(
                            color = barColor,
                            start = Offset(x, topOffset),
                            end = Offset(x, topOffset + barHeight),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                    } else {
                        drawLine(
                            color = mutedColor.copy(alpha = 0.35f),
                            start = Offset(x, size.height * 0.45f),
                            end = Offset(x, size.height * 0.55f),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "00:00",
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "24:00",
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}