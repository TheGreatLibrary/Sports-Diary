package com.sinya.projects.sportsdiary.ui.features.trainingConstructor

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.ui.features.RoundedIcon

@SuppressLint("SuspiciousIndentation")
@Composable
fun UndoIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    painter: Painter,
    isLoading: Boolean,
    modifier: Modifier,
    colorLine: Color = MaterialTheme.colorScheme.onPrimary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "undo_border")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing)
        ),
        label = "border_angle"
    )

    RoundedIcon(
        modifier = modifier
            .drawBehind {
                if (isLoading) {
                    val strokeWidth = 1.dp.toPx()
                    val radius = size.minDimension / 2f - strokeWidth / 2f
                    drawArc(
                        color = colorLine,
                        startAngle = angle,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        topLeft = Offset(strokeWidth, strokeWidth),
                        size = Size(radius * 2, radius * 2)
                    )
                }
            },
        enabled = enabled && !isLoading,
        onClick = onClick,
        description = null,
        painter = painter,
        size = 50.dp,
        isSelected = true,
        selectedContentColor = MaterialTheme.colorScheme.onPrimary,
        unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
    )
}

