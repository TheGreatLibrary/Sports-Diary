package com.sinya.projects.sportsdiary.ui.features.chart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.sinya.projects.sportsdiary.ui.features.chart.model.SelectedPointInfo
import kotlin.math.roundToInt


@Composable
fun AnimatedChartTooltip(
    selectedPoint: SelectedPointInfo?,
    formatYLabel: (Float) -> String = { it.roundToInt().toString() },
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current

    selectedPoint?.let { point ->
        val popupX = with(density) { (point.pointOffset.x + 10.dp.toPx()).toDp() }
        val popupY = with(density) { (point.pointOffset.y - 60.dp.toPx()).toDp() }

        Box(modifier = Modifier.fillMaxSize()) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(
                    with(density) { popupX.toPx() }.toInt(),
                    with(density) { popupY.toPx() }.toInt()
                ),
                onDismissRequest = onDismiss,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier
                        .widthIn(max = 150.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (point.lineLabel != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = point.lineColor,
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = point.lineLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = formatYLabel(point.yValue),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
