package com.sinya.projects.home.components.calendar.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.home.R

@Composable
internal fun MorningButton(
    modifier: Modifier = Modifier,
    @DrawableRes completeIcon: Int,
    @DrawableRes defaultIcon: Int,
    morningState: Boolean = false,
    onClick: () -> Unit,
) {
    var isHolding by remember { mutableStateOf(false) }

    val holdProgress by animateFloatAsState(
        targetValue = if (isHolding) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (isHolding) 300 else 100,
            easing = FastOutSlowInEasing
        ),
        label = "hold_progress"
    )


    LaunchedEffect(holdProgress) {
        if (holdProgress >= 1f && isHolding) {
            isHolding = false
            onClick()
        }
    }

    val containerColor by animateColorAsState(
        targetValue = if (!morningState) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.inverseSurface,
        animationSpec = tween(
            durationMillis = 220,
            easing = LinearEasing
        ),
        label = "container_color"
    )
    val contentColor by animateColorAsState(
        targetValue = if (!morningState) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.inverseOnSurface,
        label = "content_color"
    )

    val progressColor = MaterialTheme.colorScheme.secondary

    Row(
        modifier = modifier
            .height(40.dp)
            .widthIn(min = 40.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(containerColor)
            .drawBehind {
                if (morningState && holdProgress > 0f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                progressColor,
                                Color.Transparent
                            ),
                            center = Offset(-10f, size.height / 2),
                            radius = (size.width * holdProgress * 1.5f).coerceAtLeast(0.1f)
                        )
                    )
                }
            }
            .pointerInput(morningState) {
                detectTapGestures(
                    onPress = {
                        if (morningState) {
                            isHolding = true
                            tryAwaitRelease()
                            isHolding = false
                        }
                    },
                    onTap = {
                        if (!morningState) {
                            onClick()
                        }
                    }
                )
            }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        AnimatedVisibility(
            visible = !morningState,
            enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
        ) {
            Text(
                text = stringResource(R.string.home_morning_completed),
                color = contentColor,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Icon(
            painter = painterResource(id = if (morningState) completeIcon else defaultIcon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(25.dp)
        )
    }
}
