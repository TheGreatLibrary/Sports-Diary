package com.sinya.projects.sportsdiary.ui.features.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sinya.projects.sportsdiary.ui.features.map.model.DataExerciseMuscles
import com.sinya.projects.sportsdiary.ui.features.map.utils.getMuscleGradientColors
import kotlin.collections.forEach

@Composable
fun RowScope.BodyMuscles(
    bodyIcon: Painter,
    list: List<DataExerciseMuscles>,
) {
    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
        list.forEach { muscle ->
            val (centerColor, edgeColor) = remember(muscle.value, muscle.level) {
                getMuscleGradientColors(muscle.level, muscle.value)
            }

            Image(
                painter = painterResource(muscle.res),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        val muscleCenter = Offset(
                            x = size.width * muscle.centerX,
                            y = size.height * muscle.centerY
                        )

                        val radius = size.minDimension * muscle.radiusScale

                        val radialBrush = Brush.radialGradient(
                            colors = listOf(centerColor, edgeColor),
                            center = muscleCenter,
                            radius = radius
                        )

                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                brush = radialBrush,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
            )
        }
        Image(
            painter = bodyIcon,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxSize()
        )
    }
}