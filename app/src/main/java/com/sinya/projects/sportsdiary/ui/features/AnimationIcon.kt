package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AnimationIcon(
    onClick: () -> Unit,
    description: String = "",
    icon: Painter,
    isSelected: Boolean = true,
    size: Dp = 35.dp,
    shape: Shape =  MaterialTheme.shapes.extraLarge,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContainerColor: Color = Color.Transparent,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {

    val bg by animateColorAsState(
        targetValue = if (isSelected) selectedContainerColor else unselectedContainerColor,
        animationSpec = tween(durationMillis = 180),
        label = "bg"
    )
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    suspend fun bounce() {
        scale.snapTo(0.92f)
        scale.animateTo(
            1.07f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        scale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            bounce()
        } else {
            scale.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(bg, shape)
            .clickable {
                scope.launch { bounce() }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = description,
            modifier = Modifier.scale(scale.value),
            tint = if (isSelected) selectedContentColor else unselectedContentColor
        )
    }
}