package com.sinya.projects.ui.components.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


@Composable
fun Modifier.shimmerLoading(
    isLoading: Boolean = true,
    durationMillis: Int = 2000,
): Modifier = composed {
    if (!isLoading) return@composed this

    val transition = rememberInfiniteTransition(label = "shimmer")
    val animatedFloat by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    return@composed this.drawWithCache {
        val width = size.width
        val gradientWidth = width * 0.3f
        val startX = -gradientWidth + (animatedFloat * (width + gradientWidth))

        val gradientBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.05f),
                Color.White.copy(alpha = 0.1f),
                Color.White.copy(alpha = 0.2f),
                Color.White.copy(alpha = 0.1f),
                Color.White.copy(alpha = 0.05f),
            ),
            startX = startX,
            endX = startX + gradientWidth
        )

        onDrawBehind {
            drawRect(brush = gradientBrush, size = size)
        }
    }
}