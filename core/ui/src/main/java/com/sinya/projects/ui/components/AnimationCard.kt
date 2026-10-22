package com.sinya.projects.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AnimationCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colorCard: Brush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF3E3E3E),
            Color(0xFF121212),
        )
    ),
    shapeCard: Shape = MaterialTheme.shapes.small,
    content: @Composable ColumnScope.()  -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch { scale.animateTo(0.99f) }
                        tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(
                                1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    onTap = { onClick() }
                )
            }
            .background(brush = colorCard, shape = shapeCard)
            .clip(shapeCard),
        shape = shapeCard,
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent
        ),
        elevation = CardDefaults.elevatedCardElevation(0.dp)
    ) {
        content()
    }
}